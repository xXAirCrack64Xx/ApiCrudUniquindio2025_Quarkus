package co.uniquindio.crud.service.mappers;

import co.uniquindio.crud.dto.clase.ClaseRequestDTO;
import co.uniquindio.crud.dto.clase.ClaseResponseDTO;
import co.uniquindio.crud.entity.clase.Clase;
import co.uniquindio.crud.entity.clase.ClaseStatus;
import co.uniquindio.crud.entity.user.Usuario;
import org.mapstruct.*;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.Set;

/**
 * Mapper para convertir entre DTOs de Clase y la entidad Clase usando MapStruct.
 */
@Mapper(componentModel = "cdi", imports = {HashSet.class, ClaseStatus.class, Collectors.class, Usuario.class})
public interface ClaseMapper {

    /**
     * Convierte un DTO de solicitud a la entidad Clase.
     * - Ignora el id (se genera automáticamente).
     * - Inicializa el estado en CREATED.
     * - Estudiantes queda como set vacío.
     * - Profesor queda null.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claseStatus", expression = "java(ClaseStatus.CREATED)")
    @Mapping(target = "estudiantes", expression = "java(new HashSet<>())")
    @Mapping(target = "profesor", ignore = true)
    Clase toEntity(ClaseRequestDTO dto);

    /**
     * Convierte la entidad Clase a DTO de respuesta.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nombre", source = "nombre")
    @Mapping(target = "profesorId", expression = "java(clase.getProfesor() != null ? clase.getProfesor().getId() : null)")
    @Mapping(target = "estudianteIds", expression = "java(clase.getEstudiantes().stream().map(Usuario::getId).collect(Collectors.toSet()))")
    ClaseResponseDTO toResponse(Clase clase);

    /**
     * Actualiza únicamente el nombre de la entidad Clase a partir del DTO.
     * Ignora otros campos.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claseStatus", ignore = true)
    @Mapping(target = "estudiantes", ignore = true)
    @Mapping(target = "profesor", ignore = true)
    @Mapping(target = "nombre", source = "dto.nombre")
    void updateEntity(ClaseRequestDTO dto, @MappingTarget Clase entity);
}

