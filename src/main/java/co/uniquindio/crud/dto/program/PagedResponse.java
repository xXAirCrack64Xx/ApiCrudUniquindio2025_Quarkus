package co.uniquindio.crud.dto.program;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 */
public record PagedResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages
) {}
