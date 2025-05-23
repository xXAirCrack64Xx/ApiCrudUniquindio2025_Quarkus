package co.uniquindio.crud.dto.program;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de solicitud para crear/actualizar Programa.
 */
public record ProgramaRequestDTO(
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,
        String descripcion,
        @NotBlank(message = "El código fuente es obligatorio")
        String codigoFuente
) {}
