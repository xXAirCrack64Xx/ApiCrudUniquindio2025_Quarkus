package co.uniquindio.crud.exception;

public class ErrorResponse {

    public int status;
    public String message;

    public ErrorResponse() {}

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}

