package co.uniquindio.crud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UsuarioDTO {

    // Campo id de solo lectura (opcional incluirlo)
    public Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50)
    public String nombre;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 5, max = 10)
    @Pattern(regexp = "^\\d+$", message = "La cédula debe contener solo dígitos")
    public String cedula;

    @NotBlank(message = "El email es obligatorio")
    @Size(max = 50)
    @Email(message = "El email no es válido")
    public String email;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "ESTUDIANTE|PROFESOR", message = "El rol debe ser ESTUDIANTE o PROFESOR")
    public String rol;

    @Size(max = 50)
    public String clase;

    @NotBlank(message = "La clave es obligatoria")
    @Size(min = 5, max = 20)
    public String clave;
}


