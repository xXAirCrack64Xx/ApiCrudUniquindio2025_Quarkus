package co.uniquindio.crud.dto.program;


/**
 * DTO de respuesta para la ejecución de un programa.
 */
public record ExecutionResponseDTO(
        boolean success,
        String compilationOutput,
        String executionOutput
) {}
