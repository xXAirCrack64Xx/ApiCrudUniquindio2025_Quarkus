package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.comment.ComentarioRequestDTO;
import co.uniquindio.crud.dto.comment.ComentarioResponseDTO;
import co.uniquindio.crud.dto.program.CompartirConUsuariosRequest;
import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import jakarta.transaction.Transactional;

public interface ProgramaService {
    ProgramaResponseDTO crearPrograma(ProgramaRequestDTO request);

    ProgramaResponseDTO obtenerProgramaPorId(Long id);

    ProgramaResponseDTO actualizarPrograma(Long id, ProgramaRequestDTO request);

    void eliminarPrograma(Long id);

    String calificarPrograma (Long idPrograma, Long notaNueva);

    @Transactional
    ComentarioResponseDTO comentarPrograma(Long idPrograma, ComentarioRequestDTO request);

    ProgramaResponseDTO compartirConUsuarios(Long idPrograma, CompartirConUsuariosRequest request);
}
