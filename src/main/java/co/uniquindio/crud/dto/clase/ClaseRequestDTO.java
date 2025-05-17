package co.uniquindio.crud.dto.clase;

import jakarta.validation.constraints.NotBlank;

public record ClaseRequestDTO(
        @NotBlank(message = "El nombre de la clase es obligatorio")
        String nombre
) {}
