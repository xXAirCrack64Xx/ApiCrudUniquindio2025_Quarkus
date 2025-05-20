package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.exception.program.ProgramaAlreadyExistsException;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class ProgramaExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ProgramaExceptionHandler.class);

    @Provider
    public static class ProgramaNotFoundMapper implements ExceptionMapper<ProgramaNotFoundException> {
        @Override
        public Response toResponse(ProgramaNotFoundException ex) {
            LOGGER.error(ex.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @Provider
    public static class ProgramaAlreadyExistsMapper implements ExceptionMapper<ProgramaAlreadyExistsException> {
        @Override
        public Response toResponse(ProgramaAlreadyExistsException ex) {
            LOGGER.warn(ex.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();
        }
    }

}

