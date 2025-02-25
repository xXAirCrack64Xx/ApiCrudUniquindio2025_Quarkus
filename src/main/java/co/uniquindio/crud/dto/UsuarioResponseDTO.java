package co.uniquindio.crud.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDTO (
        Long id,
        String nombre,
        String email,
        String clase,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
){
}

