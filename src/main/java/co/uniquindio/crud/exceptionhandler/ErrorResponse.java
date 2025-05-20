package co.uniquindio.crud.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

// Clase auxiliar para la respuesta de error
@AllArgsConstructor
@Data
public class ErrorResponse {
    private String codigo;
    private String mensaje;
    private int status;


    // Constructor, getters y setters
}