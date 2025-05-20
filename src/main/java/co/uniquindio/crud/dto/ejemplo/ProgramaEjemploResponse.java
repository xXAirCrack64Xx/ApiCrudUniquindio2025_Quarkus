package co.uniquindio.crud.dto.ejemplo;

import java.util.List;
import java.util.Set;

public record ProgramaEjemploResponse (
        Long id,
        String titulo,
        String descripcion,
        String codigoFuente,
        String dificultad,
        String tema,
        Long autorId,
        Set<Long> clasesCompartidasIds
){
}
