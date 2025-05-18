package co.uniquindio.crud.entity.program;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idProfesor;

    @OneToOne
    @JoinColumn(name = "id_programa", referencedColumnName = "id")
    private Programa programa;

    private String comentario;




}
