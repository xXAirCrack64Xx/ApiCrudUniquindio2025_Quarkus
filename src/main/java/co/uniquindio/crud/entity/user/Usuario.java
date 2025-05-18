package co.uniquindio.crud.entity.user;

import co.uniquindio.crud.entity.clase.Clase;
import co.uniquindio.crud.entity.program.Programa;
import jakarta.persistence.*;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;


@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Data
@Accessors(chain = true)
@Entity
@Access(AccessType.FIELD)  // <--- Indicar acceso a nivel de campos
public class Usuario extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    @Enumerated(EnumType.STRING)
    private EstadoCuenta estadoCuenta;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL)
    private Set<Programa> programasCreados;

    @NotBlank
    private String clave;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    @OneToMany(mappedBy = "profesor")
    private Set<Clase> clasesComoProfesor;

    @ManyToMany(mappedBy = "estudiantes")
    private Set<Clase> clasesComoEstudiante;

}

