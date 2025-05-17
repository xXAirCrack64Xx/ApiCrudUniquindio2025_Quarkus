package co.uniquindio.crud.exceptionhandler;

import co.uniquindio.crud.dto.auth.ErrorValidacion;
import co.uniquindio.crud.dto.auth.ErrorValidacionResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Convertir las violaciones de validación en una lista de errores
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        List<ErrorValidacion> errores = violations.stream()
                .map(v -> new ErrorValidacion(
                        obtenerNombreCampo(v.getPropertyPath().toString()),
                        v.getMessage(),
                        v.getInvalidValue() != null ? v.getInvalidValue().toString() : null
                ))
                .collect(Collectors.toList());

        // Crear la respuesta de error usando el record
        ErrorValidacionResponse response = new ErrorValidacionResponse(
                400, // Código de estado HTTP
                "Error de validación en los datos proporcionados", // Mensaje general
                errores // Lista de errores
        );

        // Retornar la respuesta con código 400
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }

    // Método para extraer el nombre del campo del path de la violación
    private String obtenerNombreCampo(String propertyPath) {
        // El path puede ser algo como "createUsuario.usuarioDTO.nombre"
        // Extraemos el último segmento (el nombre del campo)
        String[] segments = propertyPath.split("\\.");
        return segments[segments.length - 1];
    }
}
