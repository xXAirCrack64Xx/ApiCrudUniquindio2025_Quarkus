package co.uniquindio.crud.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
public class Programa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    String titulo;

    String descripcion;

    String codigoFuente;

    String dificultad;


    String tema;

    @ManyToOne(optional = false)
    private Usuario autor;

    // Relaciones de compartición
    @ManyToMany
    private List<Usuario> compartidoConUsuarios;

    @ManyToMany
    private List<Clase> compartidoConClases;



}
