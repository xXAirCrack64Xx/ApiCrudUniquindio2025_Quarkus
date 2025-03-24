package co.uniquindio.crud.exception;

import lombok.Getter;

@Getter
public class UsuarioNotFoundException extends RuntimeException {

    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con el id: " + id);
    }

    // Constructor con mensaje personalizado
    public UsuarioNotFoundException(String message) {
        super(message);
    }

}
