package co.uniquindio.crud.entity.program;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProfesor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_programa")
    private Programa programa;


    private String comentario;

    private LocalDateTime fechaCreacion;

}
