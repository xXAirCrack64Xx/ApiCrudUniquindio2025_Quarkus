package co.uniquindio.crud.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para enviar un comentario a un programa.
 */
public record ComentarioRequestDTO(

        @NotNull(message = "El ID del profesor es obligatorio")
        Long idProfesor,

        @NotBlank(message = "El comentario no puede estar vacío")
        String comentario

) {}
