package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.ejemplo.CompartirConClasesRequest;
import co.uniquindio.crud.dto.ejemplo.ProgramaEjemploRequestDto;
import co.uniquindio.crud.dto.ejemplo.ProgramaEjemploResponse;
import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.entity.clase.Clase;
import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.entity.program.TipoPrograma;
import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.exception.clase.ClaseNotFoundException;
import co.uniquindio.crud.exception.program.ProgramaAlreadyExistsException;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import co.uniquindio.crud.exception.user.UsuarioNotFoundException;
import co.uniquindio.crud.repository.ClaseRepository;
import co.uniquindio.crud.repository.ProgramaRepository;
import co.uniquindio.crud.repository.UsuarioRepository;
import co.uniquindio.crud.service.interfaces.EjemploService;
import co.uniquindio.crud.service.mappers.ProgramaEjemploMapper;
import co.uniquindio.crud.utils.ResourceOwnerValidatorImpl;
import co.uniquindio.crud.utils.SecurityUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class EjemploServiceImplements implements EjemploService {
    private static final Logger LOGGER = Logger.getLogger(EjemploServiceImplements.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final ProgramaRepository programaRepository;
    private final ProgramaEjemploMapper ejemploMapper;
    private final UsuarioRepository usuarioRepository;
    private final SecurityUtils securityUtils;
    private final ClaseRepository claseRepository;
    private final ResourceOwnerValidatorImpl resourceOwner;

    @Override
    public List<ProgramaEjemploResponse> obtenerEjemplosPorTema(String tema) {
        LOGGER.infof("Obteniendo ejemplos por tema: %s", tema);

        return programaRepository.find("tipoPrograma = ?1 and tema = ?2",
                        TipoPrograma.EJEMPLO, tema)
                .list()
                .stream()
                .map(ejemploMapper::toResponse).toList();
    }

    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public ProgramaEjemploResponse crearPrograma(ProgramaEjemploRequestDto request) {
        LOGGER.infof("Creando nuevo ejemplo con título: %s", request.titulo());

        // Validar existencia previa
        if (programaRepository.find("titulo", request.titulo()).count() > 0) {
            throw new ProgramaAlreadyExistsException(request.titulo());
        }

        long autorId = securityUtils.getUserId().orElseThrow(() ->
                new UsuarioNotFoundException(0L));

        // Obtener y validar autor (debe ser profesor)
        Usuario autor = usuarioRepository.findByIdOptional(autorId)
                .orElseThrow(() -> new UsuarioNotFoundException(autorId));

        Programa ejemplo = ejemploMapper.toEntity(request, autor);
        programaRepository.persist(ejemplo);

        AUDIT_LOGGER.infof("Ejemplo creado ID=%d", ejemplo.getId());
        return ejemploMapper.toResponse(ejemplo);
    }

    @Override
    public PagedResponse<ProgramaEjemploResponse> listarEjemplos(int page, int size, String tema, String dificultad) {
        LOGGER.infof("Listando ejemplos - página %d, tamaño %d", page, size);

        PanacheQuery<Programa> query = programaRepository.find(
                "tipoPrograma = ?1 and (?2 is null or tema = ?2) and (?3 is null or dificultad = ?3)",
                TipoPrograma.EJEMPLO,
                tema,
                dificultad
        );

        List<ProgramaEjemploResponse> items = query.page(page - 1, size)
                .list()
                .stream()
                .map(ejemploMapper::toResponse).toList();

        return new PagedResponse<>(
                items,
                page,
                size,
                query.count(),
                query.pageCount()
        );
    }

    @Override
    public ProgramaEjemploResponse obtenerEjemploPorId(Long id) {
        LOGGER.infof("Obteniendo ejemplo ID=%d", id);

        return programaRepository.find("id = ?1 and tipoPrograma = ?2", id, TipoPrograma.EJEMPLO)
                .firstResultOptional()
                .map(ejemploMapper::toResponse)
                .orElseThrow(() -> new ProgramaNotFoundException(id));
    }

    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public ProgramaEjemploResponse actualizarEjemplo(Long id, ProgramaEjemploRequestDto request) {
        LOGGER.infof("Actualizando ejemplo ID=%d", id);

        Programa ejemplo = programaRepository.find("id = ?1 and tipoPrograma = ?2", id, TipoPrograma.EJEMPLO)
                .firstResultOptional()
                .orElseThrow(() -> new ProgramaNotFoundException(id));

        resourceOwner.isResourceOwner(ejemplo.getAutor().getId());

        ejemploMapper.updateEntity(request, ejemplo);
        programaRepository.persist(ejemplo);

        AUDIT_LOGGER.infof("Ejemplo actualizado ID=%d", id);
        return ejemploMapper.toResponse(ejemplo);
    }

    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public void eliminarEjemplo(Long id) {
        LOGGER.infof("Eliminando ejemplo ID=%d", id);

        resourceOwner.isResourceOwner(id);
        long deleted = programaRepository.delete("id = ?1 and tipoPrograma = ?2", id, TipoPrograma.EJEMPLO);
        if (deleted == 0) {
            throw new ProgramaNotFoundException(id);
        }

        AUDIT_LOGGER.infof("Ejemplo eliminado ID=%d", id);
    }


    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public ProgramaEjemploResponse compartirConClases(Long idPrograma, CompartirConClasesRequest request) {
        // Validar que el programa exista
        Programa programa = programaRepository.findByIdOptional(idPrograma)
                .orElseThrow(() -> new ProgramaNotFoundException(idPrograma));


        // Obtener clases válidas
        Set<Clase> clases = claseRepository
                .find("id in ?1", request.idsClases())
                .stream()
                .collect(Collectors.toSet());

        // Validar que todas las clases existan
        if (clases.size() != request.idsClases().size()) {
            List<Long> idsNoEncontrados = request.idsClases().stream()
                    .filter(id -> clases.stream().noneMatch(c -> c.getId().equals(id))).toList();
            throw new ClaseNotFoundException("Clases no encontradas: " + idsNoEncontrados);
        }

        // Agregar al conjunto de compartidos
        if (programa.getCompartidoConClases() == null) {
            programa.setCompartidoConClases(new HashSet<>());
        }
        programa.getCompartidoConClases().addAll(clases);

        programaRepository.persist(programa);
        return ejemploMapper.toResponse(programa);
    }
}
