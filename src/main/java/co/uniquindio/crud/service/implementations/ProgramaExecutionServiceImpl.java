package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.program.ExecutionResponseDTO;
import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.exception.program.ProgramExecutionException;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import co.uniquindio.crud.repository.ProgramaRepository;
import co.uniquindio.crud.service.interfaces.ProgramaExecutionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * Servicio que compila y ejecuta dinámicamente el código Java almacenado en la base de datos.
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProgramaExecutionServiceImpl implements ProgramaExecutionService {

    private static final Logger LOGGER       = Logger.getLogger(ProgramaExecutionServiceImpl.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final ProgramaRepository programaRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ExecutionResponseDTO ejecutarPrograma(Long programaId) {
        LOGGER.infof("Ejecutando programa ID=%d", programaId);

        Programa programa = programaRepository.findByIdOptional(programaId)
                .orElseThrow(() -> new ProgramaNotFoundException(programaId));

        try {
            // 1) Crear workDir y archivo fuente
            Path workDir       = crearDirectorioTemporal(programaId);
            Path fuenteFile    = escribirCodigoFuente(workDir, programa.getCodigoFuente());

            // 2) Compilar
            var compileResult  = compilarCodigo(fuenteFile, workDir);
            if (!compileResult.success()) {
                LOGGER.warnf("Error de compilación en ID=%d: %s", programaId, compileResult.output());
                return new ExecutionResponseDTO(false, compileResult.output(), null);
            }

            // 3) Ejecutar
            var execResult     = ejecutarClaseCompilada(workDir, "Main", 5);
            AUDIT_LOGGER.infof("Programa ejecutado con éxito ID=%d", programaId);
            return new ExecutionResponseDTO(true, compileResult.output(), execResult.output());

        } catch (IOException | ProgramExecutionException e) {
            LOGGER.error("Error al ejecutar el programa", e);
            throw new ProgramExecutionException("Error interno al ejecutar el programa", e);
        }
    }

    /** Crea un directorio temporal único. */
    private Path crearDirectorioTemporal(Long programaId) throws IOException {
        return Files.createTempDirectory("exec-prog-" + programaId);
    }

    /** Escribe el código fuente en Main.java dentro de workDir. */
    private Path escribirCodigoFuente(Path workDir, String codigoFuente) throws IOException {
        Path archivo = workDir.resolve("Main.java");
        Files.writeString(archivo, codigoFuente, StandardCharsets.UTF_8);
        return archivo;
    }

    /** Captura un InputStream completo en un String. */
    private String streamToString(InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        }
    }

    /**
     * Compila el archivo fuente usando el JavaCompiler de la JDK.
     * @return CompilationResult con éxito y salida de compilación.
     */
    private CompilationResult compilarCodigo(Path fuenteFile, Path workDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ProgramExecutionException(
                    "Compilador Java no disponible. Asegúrate de ejecutar con un JDK, no sólo JRE.");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int result = compiler.run(
                    /* in */    null,
                    /* out */   baos,
                    /* err */   baos,
                    /* args */  "-d", workDir.toString(), fuenteFile.toString()
            );
            String output = baos.toString(StandardCharsets.UTF_8);
            return new CompilationResult(result == 0, output);
        } catch (IOException e) {
            throw new ProgramExecutionException("Error al capturar salida del compilador", e);
        }
    }

    /**
     * Ejecuta la clase compilada Main con timeout en segundos.
     * @return ExecutionResult con éxito y salida combinada.
     */
    private ExecutionResult ejecutarClaseCompilada(Path workDir, String mainClass, int timeoutSegundos) {
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", workDir.toString(), mainClass)
                .directory(workDir.toFile());

        try {
            Process process = pb.start();
            ExecutorService executor = Executors.newSingleThreadExecutor();

            Future<String> future = executor.submit(() -> {
                String stdOut = streamToString(process.getInputStream());
                String stdErr = streamToString(process.getErrorStream());
                return stdOut + (stdErr.isEmpty() ? "" : System.lineSeparator() + stdErr);
            });

            String output;
            try {
                output = future.get(timeoutSegundos, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                process.destroyForcibly();
                throw new ProgramExecutionException("Tiempo de ejecución excedido");
            } finally {
                executor.shutdown();
            }

            return new ExecutionResult(true, output);

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new ProgramExecutionException("Error durante la ejecución del programa", e);
        }
    }

    /** Resultado de compilación. */
    private record CompilationResult(boolean success, String output) {}

    /** Resultado de ejecución. */
    private record ExecutionResult(boolean success, String output) {}
}
