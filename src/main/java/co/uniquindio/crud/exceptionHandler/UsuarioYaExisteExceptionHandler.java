package co.uniquindio.crud.exceptionHandler;

import co.uniquindio.crud.dto.ErrorResponse;
import co.uniquindio.crud.exception.UsuarioYaExisteException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UsuarioYaExisteExceptionHandler implements ExceptionMapper<UsuarioYaExisteException> {

    @Override
    public Response toResponse(UsuarioYaExisteException exception) {
        ErrorResponse errorResponse = new ErrorResponse(409, exception.getMessage());
        return Response.status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .build();
    }
}
