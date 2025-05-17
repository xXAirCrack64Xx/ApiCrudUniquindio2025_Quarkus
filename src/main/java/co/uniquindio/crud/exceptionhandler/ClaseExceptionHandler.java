package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.exception.clase.ClaseAlreadyExistsException;
import co.uniquindio.crud.exception.clase.ClaseNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ClaseExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ClaseExceptionHandler.class);

    @Provider
    public static class ClaseNotFoundExceptionMapper implements ExceptionMapper<ClaseNotFoundException> {
        @Override
        public Response toResponse(ClaseNotFoundException exception) {
            LOGGER.error("Clase no encontrada: " + exception.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(exception.getMessage())
                    .build();
        }
    }

    @Provider
    public static class ClaseAlreadyExistsExceptionMapper implements ExceptionMapper<ClaseAlreadyExistsException> {
        @Override
        public Response toResponse(ClaseAlreadyExistsException exception) {
            LOGGER.warn("Clase duplicada: " + exception.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(exception.getMessage())
                    .build();
        }
    }

}
