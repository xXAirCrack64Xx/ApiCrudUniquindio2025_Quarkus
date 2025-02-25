package co.uniquindio.crud.dto;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

public record PaginacionResponseDTO(
        @Schema(description = "Lista de usuarios en la página actual", required = true)
        List<UsuarioResponseDTO> usuarios,

        @Schema(description = "Número de página actual", example = "1", required = true)
        int paginaActual,

        @Schema(description = "Total de páginas disponibles", example = "5", required = true)
        int totalPaginas,

        @Schema(description = "Total de usuarios registrados", example = "42", required = true)
        long totalUsuarios,

        @Schema(description = "Número de resultados por página", example = "50", required = true)
        int resultadosPorPagina
) {}