package co.uniquindio.crud.entity.program;


import co.uniquindio.crud.entity.clase.Clase;
import co.uniquindio.crud.entity.user.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
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

    @Column(name = "codigoFuente", columnDefinition = "LONGTEXT", nullable = false)
    private String codigoFuente;

    private String dificultad;

    private String tema;

    //añadí estos dos atributos para gestionar los comentarios y la calificacion del programa
    private Long nota;

    @OneToMany(mappedBy = "programa", cascade = CascadeType.ALL)
    private List<Comentario> comentarios;


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
    @JoinTable(name = "programa_clase_compartido",
            joinColumns = @JoinColumn(name = "programa_id"),
            inverseJoinColumns = @JoinColumn(name = "clase_id"))
    private Set<Clase> compartidoConClases;


}
