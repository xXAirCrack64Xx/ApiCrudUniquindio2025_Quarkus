package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.program.ExecutionResponseDTO;

// Servicio de ejecución
public interface ProgramaExecutionService {

    /**
     * Compila y ejecuta el programa con el ID dado.
     * @param programaId ID del programa.
     * @return DTO con los outputs de compilación y ejecución.
     */
    ExecutionResponseDTO ejecutarPrograma(Long programaId);
}
