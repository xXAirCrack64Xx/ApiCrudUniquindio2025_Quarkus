package co.uniquindio.crud.service.auth;

import co.uniquindio.crud.entity.Usuario;
import co.uniquindio.crud.exception.UsuarioNotFoundException;
import co.uniquindio.crud.repository.UsuarioRepository;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@Slf4j
public class RoleMapperService {

    private final UsuarioRepository usuarioRepository;


    public SecurityIdentity mapRoles(SecurityIdentity identity) {
        // Extraer el email del token (asegúrate de que el claim email esté disponible)
        String email = identity.getPrincipal().getName();
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario con el correo: " + email + " no encontrado"));

        // Crear una colección modificable con los roles actuales y agregar el rol del usuario
        Set<String> newRoles = new HashSet<>(identity.getRoles());
        newRoles.add(usuario.getRol().name());

        // Construir una nueva identidad usando el builder de QuarkusSecurityIdentity
        return QuarkusSecurityIdentity.builder(identity)
                .addRoles(newRoles)
                .build();
    }

}
