package co.uniquindio.crud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @ManyToMany
    private List<Usuario> estudiantes;

    @ManyToOne(optional = false)
    private Usuario profesor;
}
