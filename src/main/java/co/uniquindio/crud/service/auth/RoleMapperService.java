package co.uniquindio.crud.service.auth;

import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.exception.user.UsuarioNotFoundException;
import co.uniquindio.crud.repository.UsuarioRepository;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger; // Cambio clave aquí

import java.util.HashSet;
import java.util.Set;

/**
 * Servicio encargado de mapear los roles y otros atributos del usuario autenticado.
 * <p>
 * Integra con el sistema de logging nativo de Quarkus para registrar operaciones
 * críticas de seguridad.
 * </p>
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class RoleMapperService {

    private static final Logger log = Logger.getLogger(RoleMapperService.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final UsuarioRepository usuarioRepository;

    /**
     * Mapea los roles y otros atributos personalizados de un usuario autenticado.
     *
     * @param identity Identidad de seguridad de Quarkus.
     * @return Nueva identidad con los roles y claims actualizados.
     */
    public SecurityIdentity mapRoles(SecurityIdentity identity) {
        String email = identity.getPrincipal().getName();
        log.infov("Asignando roles y claims para usuario: {0}", email);

        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> {
            log.warnv("Usuario no encontrado: {0}", email);
            return new UsuarioNotFoundException("Usuario no encontrado: " + email);
        });

        Set<String> newRoles = new HashSet<>(identity.getRoles());
        if (!newRoles.add(usuario.getOcupacion().name())) {
            log.debugv("Usuario {0} ya tenía rol {1}", email, usuario.getOcupacion().name());
        } else {
            log.infov("Rol {0} agregado a usuario {1}", usuario.getOcupacion().name(), email);
        }

        AUDIT_LOGGER.infov("AUDIT: Mapeo de roles | Usuario: {0} | Rol: {1}",
                email, usuario.getOcupacion().name());

        return QuarkusSecurityIdentity.builder(identity)
                .addRoles(newRoles)
                .addAttribute("id", usuario.getId())
                .build();
    }
}