package co.uniquindio.crud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ParcialUserUpdate(

        @Size(min = 3, max = 50, message = "el nombre debe tener entre 3 y 50 caracteres")
        String nombre,

        @Size(min = 5, max = 10)
        @Pattern(regexp = "^\\d+$", message = "La cédula debe contener solo dígitos")
        String cedula,

        @Size(min = 6 ,max = 50, message = "el email debe tener entre 6 y 50 caracteres")
        @Email(message = "El email no es válido")
        String email

) {
}
