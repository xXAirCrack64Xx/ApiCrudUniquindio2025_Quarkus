package co.uniquindio.crud.dto.auth;

import java.util.List;

public record ErrorValidacionResponse(

        int codigo,

        String mensaje,

        List<ErrorValidacion> errores
) {}