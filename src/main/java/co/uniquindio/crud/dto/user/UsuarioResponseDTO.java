package co.uniquindio.crud.dto.user;

import co.uniquindio.crud.entity.user.EstadoCuenta;

import java.time.LocalDateTime;

public record UsuarioResponseDTO (
        Long id,
        String nombre,
        String email,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        EstadoCuenta estadoCuenta) {
}

