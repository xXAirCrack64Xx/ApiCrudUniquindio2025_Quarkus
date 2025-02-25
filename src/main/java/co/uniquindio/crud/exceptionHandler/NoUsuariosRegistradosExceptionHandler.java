package co.uniquindio.crud.exceptionHandler;

import co.uniquindio.crud.dto.ErrorResponse;
import co.uniquindio.crud.exception.NoUsuariosRegistradosException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NoUsuariosRegistradosExceptionHandler implements ExceptionMapper<NoUsuariosRegistradosException> {

    @Override
    public Response toResponse(NoUsuariosRegistradosException exception) {
        ErrorResponse errorResponse = new ErrorResponse(204, exception.getMessage());
        return Response.status(Response.Status.NO_CONTENT)
                .entity(errorResponse)
                .build();
    }
}