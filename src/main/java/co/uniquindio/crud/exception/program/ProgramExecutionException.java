package co.uniquindio.crud.exception.program;

public class ProgramExecutionException extends RuntimeException {
    public ProgramExecutionException(String message) {
        super(message);
    }
    public ProgramExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}