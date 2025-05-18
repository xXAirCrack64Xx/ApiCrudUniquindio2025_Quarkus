package co.uniquindio.crud.service.mappers;



import co.uniquindio.crud.dto.comment.ComentarioRequestDTO;
import co.uniquindio.crud.dto.comment.ComentarioResponseDTO;
import co.uniquindio.crud.entity.program.Comentario;
import co.uniquindio.crud.entity.program.Programa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Optional;


@Mapper(componentModel = "cdi")
public interface ComentarioMapper {

    /**
     * Mapea un DTO de creación a la entidad Comentario.
     * Recibe además la entidad Programa para establecer la relación.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idProfesor", source = "dto.idProfesor")
    @Mapping(target = "comentario", source = "dto.comentario")
    @Mapping(target = "programa", source = "programa")
    @Mapping(target = "fechaCreacion", expression = "java(LocalDateTime.now())")
    Comentario toEntity(ComentarioRequestDTO dto, Programa programa);

    /**
     * Mapea una entidad Comentario a su DTO de respuesta,
     * con protección contra posibles nulls.
     */
    @Mapping(target = "id",             source = "entity.id")
    @Mapping(target = "programaId",     source = "entity.programa.id")
    @Mapping(target = "programaTitulo", source = "entity.programa.titulo")
    @Mapping(target = "profesorId",     source = "entity.idProfesor")
    @Mapping(target = "comentario",     source = "entity.comentario")
    @Mapping(target = "fechaCreacion",  source = "entity.fechaCreacion")
    ComentarioResponseDTO toResponse(Comentario entity);

    // Métodos de ayuda en caso de querer null‐safety más avanzada
    @Named("nullSafeString")
    default String nullSafe(String s) {
        return Optional.ofNullable(s).orElse("");
    }
}
