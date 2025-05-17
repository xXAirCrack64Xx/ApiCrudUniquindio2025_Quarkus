package co.uniquindio.crud.exception.clase;


/**
 * Se lanza cuando no se encuentra una clase con el ID especificado.
 */
public class ClaseNotFoundException extends RuntimeException {
    public ClaseNotFoundException(String message) {
        super(message);
    }
}
