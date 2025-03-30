package co.uniquindio.crud.service;

import co.uniquindio.crud.service.auth.RoleMapperService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger; // Cambio clave aquí

/**
 * Personaliza la identidad de seguridad de un usuario al asignarle los roles adecuados.
 * <p>
 * Utiliza el sistema de logging nativo de Quarkus (JBoss Log Manager) para registrar
 * eventos de seguridad y auditoría.
 * </p>
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CustomSecurityIdentityCustomizer implements SecurityIdentityAugmentor {

    private static final Logger logger = Logger.getLogger(CustomSecurityIdentityCustomizer.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final RoleMapperService roleMapper;

    /**
     * Asigna roles a la identidad del usuario.
     *
     * @param identity Identidad del usuario autenticado.
     * @return Identidad con los roles asignados.
     */
    @Transactional
    public SecurityIdentity updateIdentity(SecurityIdentity identity) {
        logger.infov("Asignando roles a la identidad del usuario: {0}", identity.getPrincipal().getName());
        SecurityIdentity updatedIdentity = roleMapper.mapRoles(identity);
        logger.infov("Roles asignados correctamente a la identidad: {0}", updatedIdentity.getPrincipal().getName());
        AUDIT_LOGGER.infov("AUDIT: Asignación de roles completada para usuario: {0}",
                updatedIdentity.getPrincipal().getName());
        return updatedIdentity;
    }

    /**
     * Modifica la identidad de seguridad para asignarle roles de usuario.
     *
     * @param identity Identidad del usuario autenticado.
     * @param context  Contexto de la solicitud de autenticación.
     * @return Uni que representa la identidad modificada.
     */
    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        if (identity.isAnonymous()) {
            logger.warnv("Intento de asignación de roles a identidad anónima. Se omite operación.");
            return Uni.createFrom().item(identity);
        }

        logger.infov("Procesando asignación de roles para usuario: {0}", identity.getPrincipal().getName());
        return Uni.createFrom().item(() -> {
            try {
                return updateIdentity(identity);
            } catch (Exception e) {
                logger.errorv("Error asignando roles a usuario: {0} | Error: {1}",
                        identity.getPrincipal().getName(), e.getMessage());
                AUDIT_LOGGER.errorv("AUDIT: Fallo en asignación de roles | Usuario: {0} | Error: {1}",
                        identity.getPrincipal().getName(), e.getMessage());
                return identity;
            }
        }).runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}