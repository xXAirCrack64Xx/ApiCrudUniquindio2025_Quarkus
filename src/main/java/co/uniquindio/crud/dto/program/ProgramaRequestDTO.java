package co.uniquindio.crud.dto.program;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para crear/actualizar Programa.
 */
public record ProgramaRequestDTO(
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,
        String descripcion,
        @NotBlank(message = "El código fuente es obligatorio")
        String codigoFuente,
        String dificultad,
        String tema,
        @NotNull(message = "El autor es obligatorio")
        Long autorId
) {}
