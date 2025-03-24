package co.uniquindio.crud.service;

import co.uniquindio.crud.service.auth.RoleMapperService;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.SecurityIdentityAugmentor;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class CustomSecurityIdentityCustomizer implements SecurityIdentityAugmentor {

    private final RoleMapperService roleMapper;


    @Transactional
    public SecurityIdentity updateIdentity(SecurityIdentity identity) {
        return roleMapper.mapRoles(identity);
    }

    @Override
    public Uni<SecurityIdentity> augment(SecurityIdentity identity, AuthenticationRequestContext context) {
        // Si la identidad es anónima, no intentamos mapear roles ni buscar el usuario en la BD
        if (identity.isAnonymous()) {
            return Uni.createFrom().item(identity);
        }

        return Uni.createFrom().item(() -> updateIdentity(identity))
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool());
    }
}