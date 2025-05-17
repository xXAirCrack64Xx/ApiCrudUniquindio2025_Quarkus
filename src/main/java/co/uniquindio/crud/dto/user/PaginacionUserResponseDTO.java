package co.uniquindio.crud.dto.user;

import java.util.List;

public record PaginacionUserResponseDTO(
       List<UsuarioResponseDTO> usuarios,

       int paginaActual,

       int totalPaginas,

       long totalUsuarios,

        int resultadosPorPagina
) {}