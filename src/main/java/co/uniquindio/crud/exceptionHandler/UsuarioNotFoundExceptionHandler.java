package co.uniquindio.crud.exceptionHandler;

import co.uniquindio.crud.dto.ErrorResponse;
import co.uniquindio.crud.exception.UsuarioNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UsuarioNotFoundExceptionHandler implements ExceptionMapper<UsuarioNotFoundException> {

    @Override
    public Response toResponse(UsuarioNotFoundException exception) {
        // Crear un ErrorResponse personalizado
        ErrorResponse errorResponse = new ErrorResponse(404, exception.getMessage());
        // Retornar una respuesta con el código 404 y el mensaje personalizado
        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .build();
    }
}
