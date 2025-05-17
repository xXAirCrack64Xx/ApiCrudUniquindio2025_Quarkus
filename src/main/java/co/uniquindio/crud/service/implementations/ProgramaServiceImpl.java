package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.exception.program.ProgramaAlreadyExistsException;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import co.uniquindio.crud.repository.ProgramaRepository;
import co.uniquindio.crud.resource.UsuarioResource;
import co.uniquindio.crud.service.interfaces.ProgramaService;
import co.uniquindio.crud.service.mappers.ProgramaMapper;
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

    private final ProgramaRepository programaRepository;
    private final ProgramaMapper programaMapper;

    @Override
    @Transactional
    public ProgramaResponseDTO crearPrograma(ProgramaRequestDTO request) {
        LOGGER.infof("Intentando crear programa con titulo='%s'", request.titulo());
        boolean exists = programaRepository.find("titulo", request.titulo())
                .firstResultOptional().isPresent();
        if (exists) {
            LOGGER.warnf("Ya existe un programa con titulo='%s'", request.titulo());
            throw new ProgramaAlreadyExistsException(request.titulo());
        }
        Programa entity = programaMapper.toEntity(request);
        programaRepository.persist(entity);
        AUDIT_LOGGER.infof("Programa creado con ID=%d", entity.getId());
        return programaMapper.toResponse(entity);
    }

    @Override
    public PagedResponse<ProgramaResponseDTO> listarProgramas(int page, int size) {
        LOGGER.infof("Listando programas página=%d, tamaño=%d", page, size);
        long total = programaRepository.count();
        List<ProgramaResponseDTO> items = programaRepository.findAll()
                .page(page - 1, size)
                .list()
                .stream()
                .map(programaMapper::toResponse)
                .collect(Collectors.toList());
        int totalPages = (int) Math.ceil((double) total / size);
        return new PagedResponse<>(items, page, size, total, totalPages);
    }

    @Override
    public ProgramaResponseDTO obtenerProgramaPorId(Long id) {
        LOGGER.infof("Obteniendo programa con ID=%d", id);
        Programa entity = programaRepository.findByIdOptional(id)
                .orElseThrow(() -> new ProgramaNotFoundException(id));
        return programaMapper.toResponse(entity);
    }

    @Override
    @Transactional
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
    public void eliminarPrograma(Long id) {
        LOGGER.infof("Eliminando programa con ID=%d", id);
        boolean deleted = programaRepository.deleteById(id);
        if (!deleted) {
            LOGGER.warnf("No se encontró programa con ID=%d para eliminar", id);
            throw new ProgramaNotFoundException(id);
        }
        AUDIT_LOGGER.infof("Programa eliminado con ID=%d", id);
    }
}