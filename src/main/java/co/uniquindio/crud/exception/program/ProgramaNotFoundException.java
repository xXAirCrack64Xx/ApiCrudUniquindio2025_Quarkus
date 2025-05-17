package co.uniquindio.crud.exception.program;

/**
 * Se lanza cuando no existe un programa con el ID especificado.
 */
public class ProgramaNotFoundException extends RuntimeException {
    public ProgramaNotFoundException(Long id) {
        super("Programa con id " + id + " no fue encontrado.");
    }
}
