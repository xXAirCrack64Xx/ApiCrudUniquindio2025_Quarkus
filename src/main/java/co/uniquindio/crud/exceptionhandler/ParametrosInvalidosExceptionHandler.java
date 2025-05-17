package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.dto.auth.ErrorResponse;
import co.uniquindio.crud.exception.user.ParametrosInvalidosException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ParametrosInvalidosExceptionHandler implements ExceptionMapper<ParametrosInvalidosException> {
    @Override
    public Response toResponse(ParametrosInvalidosException exception) {
        ErrorResponse error = new ErrorResponse(400, exception.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
    }
}
