package co.uniquindio.crud.dto;

import co.uniquindio.crud.entity.EstadoCuenta;

import java.time.LocalDateTime;

public record UsuarioResponseDTO (
        Long id,
        String nombre,
        String email,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        EstadoCuenta estadoCuenta) {
}

