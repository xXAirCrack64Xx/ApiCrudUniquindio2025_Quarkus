package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.clase.ClaseRequestDTO;
import co.uniquindio.crud.dto.clase.ClaseResponseDTO;
import co.uniquindio.crud.entity.clase.Clase;
import co.uniquindio.crud.entity.clase.ClaseStatus;
import co.uniquindio.crud.exception.clase.ClaseAlreadyExistsException;
import co.uniquindio.crud.exception.clase.ClaseNotFoundException;
import co.uniquindio.crud.repository.ClaseRepository;
import co.uniquindio.crud.resource.ClaseResource;
import co.uniquindio.crud.service.interfaces.ClaseService;
import co.uniquindio.crud.service.mappers.ClaseMapper;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de {@link ClaseService} que provee operaciones CRUD para la gestión de clases.
 * <p>
 * Esta clase maneja la lógica de negocio relacionada con las entidades Clase, incluyendo validaciones,
 * transformaciones DTO y registro de auditoría. Utiliza inyección de dependencias para acceder al repositorio
 * y al mapper correspondiente.
 * </p>
 *
 * @ApplicationScoped Indica que la instancia de esta clase será manejada como un bean de aplicación en el contenedor CDI
 * @RequiredArgsConstructor Genera un constructor con parámetros para las dependencias inyectadas
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ClaseServiceImplements implements ClaseService {

    private final ClaseRepository claseRepository;
    private final ClaseMapper claseMapper;
    private static final Logger LOGGER = Logger.getLogger(ClaseResource.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");


    /**
     * Crea una nueva clase verificando previamente la unicidad del nombre.
     * <p>
     * Registra eventos en el logger de auditoría después de la creación exitosa.
     * </p>
     *
     * @param request DTO con los datos para la creación de la clase
     * @return DTO con los datos de la clase creada
     * @throws ClaseAlreadyExistsException si ya existe una clase con el mismo nombre
     * @Transactional La ejecución se realiza dentro de una transacción
     */
    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public ClaseResponseDTO crearClase(ClaseRequestDTO request) {
        LOGGER.infof("Intentando crear clase con nombre='%s'", request.nombre());
        // Verificar existencia previa
        boolean exists = claseRepository.find("nombre", request.nombre()).firstResultOptional().isPresent();
        if (exists) {
            LOGGER.warnf("Ya existe una clase con nombre='%s'", request.nombre());
            throw new ClaseAlreadyExistsException("La clase con nombre '" + request.nombre() + "' ya existe");
        }
        // Mapeo a entidad y persistencia
        Clase entity = claseMapper.toEntity(request);
        claseRepository.persist(entity);
        AUDIT_LOGGER.infof("Clase creada con ID=%d", entity.getId());
        return claseMapper.toResponse(entity);
    }


    /**
     * Obtiene todas las clases existentes en formato DTO.
     *
     * @return Lista de DTOs con todas las clases registradas
     */
    @Override
    @Authenticated
    public List<ClaseResponseDTO> listarClases() {
        LOGGER.info("Listando todas las clases");
        List<Clase> clases = claseRepository.findAllClases();
        return clases.stream()
                .map(claseMapper::toResponse).toList();
    }


    /**
     * Busca una clase por su identificador único.
     *
     * @param id Identificador único de la clase
     * @return DTO con los datos de la clase encontrada
     * @throws ClaseNotFoundException si no se encuentra la clase con el ID especificado
     */
    @Override
    @Authenticated
    public ClaseResponseDTO obtenerClasePorId(Long id) {
        LOGGER.infof("Obteniendo clase con ID=%d", id);
        Clase entity = claseRepository.findClaseById(id)
                .orElseThrow(() -> new ClaseNotFoundException("Clase con ID '" + id + "' no encontrada"));
        return claseMapper.toResponse(entity);
    }


    /**
     * Actualiza los datos de una clase existente.
     * <p>
     * Registra eventos en el logger de auditoría después de la actualización exitosa.
     * </p>
     *
     * @param id Identificador único de la clase a actualizar
     * @param request DTO con los nuevos datos para la clase
     * @return DTO con los datos actualizados de la clase
     * @throws ClaseNotFoundException si no se encuentra la clase con el ID especificado
     * @Transactional La ejecución se realiza dentro de una transacción
     */
    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public ClaseResponseDTO actualizarClase(Long id, ClaseRequestDTO request) {
        LOGGER.infof("Actualizando clase con ID=%d", id);
        Clase entity = claseRepository.findClaseById(id)
                .orElseThrow(() -> new ClaseNotFoundException("Clase con ID '" + id + "' no encontrada"));
        claseMapper.updateEntity(request, entity);
        claseRepository.flush(); // aplica los cambios
        AUDIT_LOGGER.infof("Clase actualizada con ID=%d", entity.getId());
        return claseMapper.toResponse(entity);
    }


    /**
     * Realiza una eliminación lógica de una clase cambiando su estado a DELETED.
     * <p>
     * Registra eventos en el logger de auditoría después de la eliminación exitosa.
     * </p>
     *
     * @param id Identificador único de la clase a eliminar
     * @throws ClaseNotFoundException si no se encuentra la clase con el ID especificado
     * @Transactional La ejecución se realiza dentro de una transacción
     */
    @Override
    @Transactional
    @RolesAllowed("PROFESOR")
    public void eliminarClase(Long id) {
        LOGGER.infof("Eliminando clase con ID=%d", id);
        Clase claseDeleted = claseRepository.findClaseById(id)
                .orElseThrow(() -> new ClaseNotFoundException("Clase con ID '" + id + "' no encontrada"));
        claseDeleted.setClaseStatus(ClaseStatus.DELETED);
        claseRepository.flush();
        AUDIT_LOGGER.infof("Clase eliminada con ID=%d", id);
    }
}
