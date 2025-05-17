package co.uniquindio.crud.service.mappers;

import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.entity.program.EstadoPrograma;
import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.entity.clase.Clase;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre DTOs de Programa y la entidad Programa usando MapStruct.
 */
@Mapper(componentModel = "cdi", imports = {HashSet.class, EstadoPrograma.class, Collectors.class, Usuario.class, Clase.class})
public interface ProgramaMapper {

    /**
     * Convierte un DTO de solicitud a la entidad Programa.
     * - Ignora el id (se genera automáticamente).
     * - Inicializa el estado en CREATED.
     * - autor queda null (será seteado en el servicio).
     * - compartidoConUsuarios y compartidoConClases como sets vacíos.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoPrograma", expression = "java(EstadoPrograma.CREATED)")
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "compartidoConUsuarios", expression = "java(new HashSet<>())")
    @Mapping(target = "compartidoConClases", expression = "java(new HashSet<>())")
    Programa toEntity(ProgramaRequestDTO dto);

    /**
     * Convierte la entidad Programa a DTO de respuesta.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "titulo", source = "titulo")
    @Mapping(target = "descripcion", source = "descripcion")
    @Mapping(target = "codigoFuente", source = "codigoFuente")
    @Mapping(target = "dificultad", source = "dificultad")
    @Mapping(target = "tema", source = "tema")
    @Mapping(target = "autorId", expression = "java(programa.getAutor()!=null?programa.getAutor().getId():null)")
    @Mapping(target = "usuarioIds", expression = "java(programa.getCompartidoConUsuarios().stream().map(Usuario::getId).collect(Collectors.toSet()))")
    @Mapping(target = "claseIds", expression = "java(programa.getCompartidoConClases().stream().map(Clase::getId).collect(Collectors.toSet()))")
    ProgramaResponseDTO toResponse(Programa programa);

    /**
     * Actualiza la entidad Programa a partir del DTO.
     * - Ignora id, estadoPrograma, autor y relaciones de compartido.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoPrograma", ignore = true)
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "compartidoConUsuarios", ignore = true)
    @Mapping(target = "compartidoConClases", ignore = true)
    @Mapping(target = "titulo", source = "dto.titulo")
    @Mapping(target = "descripcion", source = "dto.descripcion")
    @Mapping(target = "codigoFuente", source = "dto.codigoFuente")
    @Mapping(target = "dificultad", source = "dto.dificultad")
    @Mapping(target = "tema", source = "dto.tema")
    void updateEntity(ProgramaRequestDTO dto, @MappingTarget Programa programa);
}

