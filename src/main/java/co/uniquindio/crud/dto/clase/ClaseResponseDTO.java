package co.uniquindio.crud.dto.clase;

import java.util.Set;

/**
 * DTO de respuesta para exponer datos de Clase al cliente.
 */
public record ClaseResponseDTO(
        Long id,
        String nombre,
        Long profesorId,
        Set<Long> estudianteIds
) {}
