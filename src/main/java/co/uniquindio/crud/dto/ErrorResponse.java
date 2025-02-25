package co.uniquindio.crud.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "Código de estado HTTP", example = "400")
        int codigo,

        @Schema(description = "Mensaje de error", example = "Parámetros inválidos")
        String mensaje
) {}

