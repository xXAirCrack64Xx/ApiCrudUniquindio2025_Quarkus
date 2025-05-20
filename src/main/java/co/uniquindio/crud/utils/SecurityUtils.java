package co.uniquindio.crud.utils;

import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Utilidades de seguridad para leer atributos de la identidad.
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class SecurityUtils {


    private final SecurityIdentity identity;

    /**
     * Extrae el atributo "id" de la identidad (establecido en RoleMapperService).
     *
     * @return Optional con el userId si existe y es un Number, vacío en otro caso.
     */
    public Optional<Long> getUserId() {
        Object raw = identity.getAttribute("id");
        if (raw instanceof Number) {
            return Optional.of(((Number) raw).longValue());
        }
        if (raw instanceof String) {
            try {
                return Optional.of(Long.parseLong((String) raw));
            } catch (NumberFormatException e) {
                // no es un número válido
            }
        }
        return Optional.empty();
    }
}

