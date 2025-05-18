package co.uniquindio.crud.dto.program;

import java.util.List;
import java.util.Set;

public record ProgramaResponseDTO(
        Long id,
        String titulo,
        String descripcion,
        String codigoFuente,
        String dificultad,
        String tema,
        Long autorId,
        List<String> comentarios,
        Set<Long> usuariosCompartidosIds,
        Set<Long> clasesCompartidasIds,

        Long nota

) {}

