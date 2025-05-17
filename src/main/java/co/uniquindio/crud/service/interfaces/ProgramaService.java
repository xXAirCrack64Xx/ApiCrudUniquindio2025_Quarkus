package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;

public interface ProgramaService {
    ProgramaResponseDTO crearPrograma(ProgramaRequestDTO request);

    PagedResponse<ProgramaResponseDTO> listarProgramas(int page, int size);

    ProgramaResponseDTO obtenerProgramaPorId(Long id);

    ProgramaResponseDTO actualizarPrograma(Long id, ProgramaRequestDTO request);

    void eliminarPrograma(Long id);
}
