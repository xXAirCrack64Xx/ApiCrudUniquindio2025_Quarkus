package co.uniquindio.crud.service.mappers;

import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.entity.program.EstadoPrograma;
import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.entity.clase.Clase;
import org.mapstruct.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre DTOs de Programa y la entidad Programa usando MapStruct.
 */
@Mapper(componentModel = "cdi", imports = {Collectors.class, Collections.class, Usuario.class, Clase.class, HashSet.class,
                                                EstadoPrograma.class})
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
    @Mapping(target = "autor", source = "autor")
    @Mapping(target = "compartidoConUsuarios", expression = "java(new HashSet<>())")
    @Mapping(target = "compartidoConClases", expression = "java(new HashSet<>())")
    Programa toEntity(ProgramaRequestDTO dto, Usuario autor);

    /**
     * Convierte la entidad Programa a DTO de respuesta.
     */
    @Mapping(target = "id",         source = "programa.id")
    @Mapping(target = "titulo",     source = "programa.titulo")
    @Mapping(target = "descripcion",source = "programa.descripcion")
    @Mapping(target = "codigoFuente", source = "programa.codigoFuente")
    @Mapping(target = "dificultad", source = "programa.dificultad")
    @Mapping(target = "tema",       source = "programa.tema")
    @Mapping(target = "nota",       source = "programa.nota")

    @Mapping(
            target = "autorId",
            expression = "java(programa.getAutor() != null ? programa.getAutor().getId() : null)"
    )
    @Mapping(
            target = "comentarios",
            expression = "java(programa.getComentarios() != null ? programa.getComentarios().stream().map(c -> c.getComentario()).collect(Collectors.toList()) : Collections.emptyList())"
    )
    @Mapping(
            target = "usuariosCompartidosIds",
            expression = "java(programa.getCompartidoConUsuarios() != null ? programa.getCompartidoConUsuarios().stream().map(Usuario::getId).collect(Collectors.toSet()) : Collections.emptySet())"
    )
    @Mapping(
            target = "clasesCompartidasIds",
            expression = "java(programa.getCompartidoConClases()  != null ? programa.getCompartidoConClases().stream().map(Clase::getId).collect(Collectors.toSet()) : Collections.emptySet())"
    )
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

