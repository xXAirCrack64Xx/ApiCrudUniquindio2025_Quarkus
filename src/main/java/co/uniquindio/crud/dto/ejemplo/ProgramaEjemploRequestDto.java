package co.uniquindio.crud.dto.ejemplo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProgramaEjemploRequestDto (
        @NotBlank(message = "El titulo es obligatorio")
        String titulo,
        String descripcion,
        @NotBlank(message = "El código fuente es obligatorio")
        String codigoFuente,
        @NotBlank(message = "La dificultad es obligatoria")
        String dificultad,
        @NotBlank(message = "El tema es obligatorio")
        String tema
){
}
