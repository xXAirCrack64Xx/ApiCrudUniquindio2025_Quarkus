package co.uniquindio.crud.exception.program;

/**
 * Se lanza cuando ya existe un programa con el mismo titulo.
 */
public class ProgramaAlreadyExistsException extends RuntimeException {
    public ProgramaAlreadyExistsException(String titulo) {
        super("Ya existe un programa con el titulo: " + titulo);
    }
}
