package co.uniquindio.crud.dto.ejemplo;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CompartirConClasesRequest(
        @NotEmpty List<Long> idsClases
) {}
