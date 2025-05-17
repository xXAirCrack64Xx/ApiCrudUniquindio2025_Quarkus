package co.uniquindio.crud.entity.clase;

import co.uniquindio.crud.entity.user.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @Enumerated(EnumType.STRING)
    private ClaseStatus claseStatus;

    @ManyToMany
    private Set<Usuario> estudiantes;

    @ManyToOne(optional = false)
    private Usuario profesor;

}
