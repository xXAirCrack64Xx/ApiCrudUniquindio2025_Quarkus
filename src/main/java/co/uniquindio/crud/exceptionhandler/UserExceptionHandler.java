package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.dto.auth.ErrorResponse;
import co.uniquindio.crud.exception.user.NoUsuariosRegistradosException;
import co.uniquindio.crud.exception.user.UsuarioNotFoundException;
import co.uniquindio.crud.exception.user.UsuarioYaExisteException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserExceptionHandler {

    @Provider
    public static class NoUsuariosRegistradosExceptionHandler implements ExceptionMapper<NoUsuariosRegistradosException> {

        @Override
        public Response toResponse(NoUsuariosRegistradosException exception) {
            ErrorResponse errorResponse = new ErrorResponse(204, exception.getMessage());
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(errorResponse)
                    .build();
        }
    }

    @Provider
    public static class UsuarioNotFoundExceptionHandler implements ExceptionMapper<UsuarioNotFoundException> {

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

    @Provider
    public static class UsuarioYaExisteExceptionHandler implements ExceptionMapper<UsuarioYaExisteException> {

        @Override
        public Response toResponse(UsuarioYaExisteException exception) {
            ErrorResponse errorResponse = new ErrorResponse(409, exception.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(errorResponse)
                    .build();
        }
    }

}
