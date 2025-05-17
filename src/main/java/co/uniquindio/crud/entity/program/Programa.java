package co.uniquindio.crud.entity.program;


import co.uniquindio.crud.entity.clase.Clase;
import co.uniquindio.crud.entity.user.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Entity
@Data
public class Programa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titulo;

    private String descripcion;

    private String codigoFuente;

    private String dificultad;


    private String tema;

    @Enumerated(EnumType.STRING)
    private EstadoPrograma estadoPrograma;

    @ManyToOne(optional = false)
    private Usuario autor;

    // Relaciones de compartición
    @ManyToMany
    @JoinTable(name = "programa_usuario_compartido",
            joinColumns = @JoinColumn(name = "programa_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> compartidoConUsuarios;

    @ManyToMany
    private Set<Clase> compartidoConClases;

}
