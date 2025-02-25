package co.uniquindio.crud.entity;

import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.jboss.resteasy.reactive.DateFormat;

import java.time.LocalDateTime;

@Entity
@Data
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    private String nombre;

    @NotBlank
    @Size(min = 5, max = 10)
    @Pattern(regexp = "^\\d+$")
    private String cedula;

    @NotBlank
    @Size(min = 6 ,max = 50)
    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private OcupacionUsuario ocupacion;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    private boolean cuentaActivada = false;

    @Size(max = 50)
    private String clase;

    @NotBlank
    @Size(min = 5, max = 20)
    private String clave;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

}

