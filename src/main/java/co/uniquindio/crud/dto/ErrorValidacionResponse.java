package co.uniquindio.crud.dto;


import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.List;

public record ErrorValidacionResponse(
        @Schema(description = "Código de estado HTTP", example = "400")
        int codigo,

        @Schema(description = "Mensaje general del error", example = "Error de validación en los datos proporcionados")
        String mensaje,

        @Schema(description = "Lista de errores de validación")
        List<ErrorValidacion> errores
) {}