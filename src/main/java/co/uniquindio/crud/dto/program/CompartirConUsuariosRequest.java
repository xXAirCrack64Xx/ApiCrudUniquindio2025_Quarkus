package co.uniquindio.crud.dto.program;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record CompartirConUsuariosRequest(
        @NotEmpty Set<Long> idsUsuarios
) {}
