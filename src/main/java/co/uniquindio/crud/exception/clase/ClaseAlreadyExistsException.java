package co.uniquindio.crud.exception.clase;


/**
 * Se lanza cuando ya existe una clase con el mismo nombre.
 */
public class ClaseAlreadyExistsException extends RuntimeException {
    public ClaseAlreadyExistsException(String message) {
        super(message);
    }
}

