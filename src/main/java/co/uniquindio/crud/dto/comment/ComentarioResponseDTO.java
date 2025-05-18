package co.uniquindio.crud.dto.comment;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para un Comentario.
 */
public record ComentarioResponseDTO(
        Long id,
        Long programaId,
        String programaTitulo,
        Long profesorId,
        String comentario,
        LocalDateTime fechaCreacion
) {}

