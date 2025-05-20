package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.comment.ComentarioRequestDTO;
import co.uniquindio.crud.dto.comment.ComentarioResponseDTO;
import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import co.uniquindio.crud.entity.program.Comentario;
import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.exception.program.ProgramaAlreadyExistsException;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import co.uniquindio.crud.exception.user.UsuarioNotFoundException;
import co.uniquindio.crud.repository.ComentarioRepository;
import co.uniquindio.crud.repository.ProgramaRepository;
import co.uniquindio.crud.repository.UsuarioRepository;
import co.uniquindio.crud.service.emailService.EmailService;
import co.uniquindio.crud.service.interfaces.ProgramaService;
import co.uniquindio.crud.service.mappers.ComentarioMapper;
import co.uniquindio.crud.service.mappers.ProgramaMapper;
import co.uniquindio.crud.utils.SecurityUtils;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProgramaServiceImpl implements ProgramaService {

    private static final Logger LOGGER = Logger.getLogger(ProgramaServiceImpl.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final ComentarioMapper comentarioMapper;
    private final ProgramaRepository programaRepository;
    private final ProgramaMapper programaMapper;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;
    private final ComentarioRepository comentarioRepository;
    private final EmailService emailService;


    @Override
    @Transactional
    @Authenticated
    public ProgramaResponseDTO crearPrograma(ProgramaRequestDTO request) {
        LOGGER.infof("Intentando crear programa con titulo='%s'", request.titulo());
        boolean exists = programaRepository.find("titulo", request.titulo())
                .firstResultOptional().isPresent();
        if (exists) {
            LOGGER.warnf("Ya existe un programa con titulo='%s'", request.titulo());
            throw new ProgramaAlreadyExistsException(request.titulo());
        }

        long autorId = securityUtils.getUserId().orElseThrow(() ->
                new UsuarioNotFoundException(0L));

        Usuario autor = usuarioRepository.findActiveById(autorId);
        Programa entity = programaMapper.toEntity(request, autor);
        programaRepository.persist(entity);
        AUDIT_LOGGER.infof("Programa creado con ID=%d", entity.getId());
        return programaMapper.toResponse(entity);
    }

    @Override
    @Authenticated
    public PagedResponse<ProgramaResponseDTO> listarProgramas(int page, int size) {
        LOGGER.infof("Listando programas página=%d, tamaño=%d", page, size);
        long total = programaRepository.count();
        List<ProgramaResponseDTO> items = programaRepository.findAll()
                .page(page - 1, size)
                .list()
                .stream()
                .map(programaMapper::toResponse).toList();
        int totalPages = (int) Math.ceil((double) total / size);
        return new PagedResponse<>(items, page, size, total, totalPages);
    }

    @Override
    @Authenticated
    public ProgramaResponseDTO obtenerProgramaPorId(Long id) {
        LOGGER.infof("Obteniendo programa con ID=%d", id);
        Programa entity = programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProgramaNotFoundException(id));
        return programaMapper.toResponse(entity);
    }

    @Override
    @Transactional
    @Authenticated
    public ProgramaResponseDTO actualizarPrograma(Long id, ProgramaRequestDTO request) {
        LOGGER.infof("Actualizando programa con ID=%d", id);
        Programa entity = programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProgramaNotFoundException(id));
        programaMapper.updateEntity(request, entity);
        programaRepository.flush();
        AUDIT_LOGGER.infof("Programa actualizado con ID=%d", id);
        return programaMapper.toResponse(entity);
    }

    @Override
    @Transactional
    @Authenticated
    public void eliminarPrograma(Long id) {
        LOGGER.infof("Eliminando programa con ID=%d", id);
        boolean deleted = programaRepository.deleteById(id);
        if (!deleted) {
            LOGGER.warnf("No se encontró programa con ID=%d para eliminar", id);
            throw new ProgramaNotFoundException(id);
        }
        AUDIT_LOGGER.infof("Programa eliminado con ID=%d", id);
    }


    @Transactional
    @Override
    @RolesAllowed("PROFESOR")
    public String calificarPrograma(Long idPrograma, Long notaNueva) {

        LOGGER.infof("Calificando programa con ID=%d", idPrograma);

        if (notaNueva < 0 || notaNueva > 5) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 5.");
        }

        Programa entity = programaRepository.findByIdOptional(idPrograma)
                .orElseThrow(() -> new ProgramaNotFoundException(idPrograma));

        entity.setNota(notaNueva);
        programaRepository.flush();

        AUDIT_LOGGER.infof("Nota actualizada a %d para el programa con ID=%d", notaNueva, idPrograma);

        // apartado de envío del correo
        Usuario user = entity.getAutor();
        emailService.enviarCorreo(user.getEmail(), "Su programa ha sido calificado por el profesor, nota: " + notaNueva);


        return "Nota actualizada correctamente para el programa '" + entity.getTitulo() + "', nota: " + notaNueva;

    }

    @Transactional
    @Override
    @RolesAllowed("PROFESOR")
    public ComentarioResponseDTO comentarPrograma(Long idPrograma, ComentarioRequestDTO request) {
        LOGGER.infof("Inicio del método comentarPrograma para Programa ID=%d, Profesor ID=%d", idPrograma);

        try {
            // Buscar el programa, si no existe lanza excepción
            Programa entity = programaRepository.findByIdOptional(idPrograma)
                    .orElseThrow(() -> new ProgramaNotFoundException(idPrograma));

            LOGGER.debugf("Programa encontrado: %s (ID=%d)", entity.getTitulo(), idPrograma);

            // Crear nuevo comentario y asignar datos
            long autorid = securityUtils.getUserId().orElseThrow(() ->
                    new UsuarioNotFoundException(Long.valueOf(0)));
            Comentario coment = comentarioMapper.toEntity(request, entity, autorid);

            // Persistir comentario en base de datos
            comentarioRepository.persist(coment);
            LOGGER.debugf("Comentario persistido para Programa ID=%d por Profesor ID=%d", idPrograma);

            // Asociar comentario al programa y actualizar entidad
            entity.getComentarios().add(coment);
            programaRepository.flush();

            AUDIT_LOGGER.infof("Comentario actualizado para el programa con ID=%d", idPrograma);

            LOGGER.infof("Comentario actualizado correctamente para el programa '%s'", entity.getTitulo());

            // apartado de envío del correo
            Usuario user = entity.getAutor();
            emailService.enviarCorreo(user.getEmail(), "Su programa ha sido comentado por el profesor: " +
                    user.getNombre() + ", comentario: '" + coment.getComentario() + "'");


            return comentarioMapper.toResponse(coment);

        } catch (ProgramaNotFoundException e) {
            LOGGER.errorf("No se encontró el programa con ID=%d", idPrograma);
            throw e; // O manejarlo de acuerdo a tu política de errores

        } catch (Exception e) {
            LOGGER.errorf(e, "Error inesperado al comentar programa con ID=%d", idPrograma);
            throw e; // O manejar la excepción de otra forma
        }
    }


}