package co.uniquindio.crud.dto.program;

import java.util.List;
import java.util.Set;

public record ProgramaResponseDTO(
        Long id,
        String titulo,
        String descripcion,
        String codigoFuente,
        Long autorId,
        List<String> comentarios,
        Set<Long> usuariosCompartidosIds,

        Long nota

) {}

