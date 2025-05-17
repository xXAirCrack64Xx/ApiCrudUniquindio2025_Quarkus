package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.clase.ClaseRequestDTO;
import co.uniquindio.crud.dto.clase.ClaseResponseDTO;

import java.util.List;

public interface ClaseService {
    ClaseResponseDTO crearClase(ClaseRequestDTO request);

    List<ClaseResponseDTO> listarClases();

    ClaseResponseDTO obtenerClasePorId(Long id);

    ClaseResponseDTO actualizarClase(Long id, ClaseRequestDTO request);

    void eliminarClase(Long id);
}
