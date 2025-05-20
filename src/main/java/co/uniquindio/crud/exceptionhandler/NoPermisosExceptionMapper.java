package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.exception.auth.NoPermisosException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NoPermisosExceptionMapper implements ExceptionMapper<NoPermisosException> {

    @Override
    public Response toResponse(NoPermisosException exception) {
        ErrorResponse error = new ErrorResponse(
                "PERMISO_DENEGADO",
                exception.getMessage(),
                Response.Status.FORBIDDEN.getStatusCode()
        );
        return Response.status(Response.Status.FORBIDDEN)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}

