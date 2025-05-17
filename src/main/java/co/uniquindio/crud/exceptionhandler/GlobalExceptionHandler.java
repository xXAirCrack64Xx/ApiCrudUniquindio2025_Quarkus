package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.dto.auth.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

@Provider
@Slf4j
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        // Log del error (opcional, pero recomendado para depuración)
        log.error("Error no manejado: {}", exception.getMessage(), exception);

        // Crear un ErrorResponse personalizado
        ErrorResponse errorResponse = new ErrorResponse(
                500, // Código de estado HTTP 500 (Error interno del servidor)
                "Error interno del servidor. Inténtelo nuevamente más tarde." // Mensaje genérico
        );

        // Retornar una respuesta con el código 500 y el mensaje personalizado
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorResponse)
                .build();
    }
}
