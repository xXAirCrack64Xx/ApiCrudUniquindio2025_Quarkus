package co.uniquindio.crud.exception;

import lombok.Getter;

@Getter
public class UsuarioNotFoundException extends RuntimeException {

    private final Long id;

    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con el id: " + id);
        this.id = id;
    }

}
