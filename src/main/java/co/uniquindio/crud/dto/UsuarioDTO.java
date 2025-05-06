package co.uniquindio.crud.dto;

import jakarta.validation.constraints.*;

public record UsuarioDTO (

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 50, message = "el nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @NotBlank(message = "La cédula es obligatoria")
        @Size(min = 5, max = 10)
        @Pattern(regexp = "^\\d+$", message = "La cédula debe contener solo dígitos")
        String cedula,

        @NotBlank(message = "El email es obligatorio")
        @Size(min = 6 ,max = 50, message = "el email debe tener entre 6 y 50 caracteres")
        @Email(message = "El email no es válido")
        String email,

        @NotBlank(message = "La ocupación es obligatoria")
        @Pattern(regexp = "ESTUDIANTE|PROFESOR", message = "La ocupación debe ser ESTUDIANTE o PROFESOR")
        String ocupacion,

        @NotBlank(message = "La clave es obligatoria")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "La clave debe contener al menos un dígito, una mayúscula y una minúscula")
        @Size(min = 8, max = 20)
        String clave
) {
}


