package co.uniquindio.crud.exception;

public class UsuarioYaExisteException extends RuntimeException {
    public UsuarioYaExisteException(String message) {
        super(message);
    }
}