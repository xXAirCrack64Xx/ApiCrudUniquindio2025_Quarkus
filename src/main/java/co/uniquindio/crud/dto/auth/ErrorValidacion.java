package co.uniquindio.crud.dto.auth;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record ErrorValidacion(
        @Schema(description = "Campo que falló la validación", example = "email")
        String campo,

        @Schema(description = "Mensaje de error de validación", example = "El email no es válido")
        String mensaje,

        @Schema(description = "Valor proporcionado que falló la validación", example = "usuario@dominio")
        String valor
) {}
