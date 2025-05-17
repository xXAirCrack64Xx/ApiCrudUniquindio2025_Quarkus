package co.uniquindio.crud.dto.program;

import java.util.Set;

public record ProgramaResponseDTO(
        Long id,
        String titulo,
        String descripcion,
        String codigoFuente,
        String dificultad,
        String tema,
        Long autorId,
        Set<Long> usuarioIds,
        Set<Long> claseIds
) {}

