package co.uniquindio.crud.dto;

import jakarta.validation.constraints.*;

public record UsuarioDTO (

        Long id,

        @NotNull(message = "El nombre es obligatorio")
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "el nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @NotNull(message = "La cédula es obligatoria")
        @NotBlank(message = "La cédula es obligatoria")
        @Size(min = 5, max = 10)
        @Pattern(regexp = "^\\d+$", message = "La cédula debe contener solo dígitos")
        String cedula,

        @NotNull(message = "El email es obligatorio")
        @NotBlank(message = "El email es obligatorio")
        @Size(min = 6 ,max = 50, message = "el email debe tener entre 6 y 50 caracteres")
        @Email(message = "El email no es válido")
        String email,

        @NotNull(message = "La ocupación es obligatoria")
        @NotBlank(message = "La ocupación es obligatoria")
        @Pattern(regexp = "ESTUDIANTE|PROFESOR", message = "La ocupación debe ser ESTUDIANTE o PROFESOR")
        String ocupacion,

        @Size(max = 50, message = "la clase maximo puede contener 50 caracteres")
        String clase,

        @NotNull(message = "La clave es obligatoria")
        @NotBlank(message = "La clave es obligatoria")
        @Size(min = 5, max = 20)
        String clave
) {
}


