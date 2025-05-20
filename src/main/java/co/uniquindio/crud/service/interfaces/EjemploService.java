package co.uniquindio.crud.service.interfaces;

import co.uniquindio.crud.dto.ejemplo.CompartirConClasesRequest;
import co.uniquindio.crud.dto.ejemplo.ProgramaEjemploRequestDto;
import co.uniquindio.crud.dto.ejemplo.ProgramaEjemploResponse;
import co.uniquindio.crud.dto.program.PagedResponse;

import java.util.List;

public interface EjemploService {
    List<ProgramaEjemploResponse> obtenerEjemplosPorTema(String tema);

    ProgramaEjemploResponse crearPrograma(ProgramaEjemploRequestDto request);

    PagedResponse<ProgramaEjemploResponse> listarEjemplos(int page, int size, String tema, String dificultad);

    ProgramaEjemploResponse obtenerEjemploPorId(Long id);

    ProgramaEjemploResponse actualizarEjemplo(Long id, ProgramaEjemploRequestDto request);

    void eliminarEjemplo(Long id);

    ProgramaEjemploResponse compartirConClases(Long idPrograma, CompartirConClasesRequest request);
}
