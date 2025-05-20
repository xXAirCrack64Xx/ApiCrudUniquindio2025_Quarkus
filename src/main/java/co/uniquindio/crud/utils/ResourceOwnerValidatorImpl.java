package co.uniquindio.crud.utils;

import co.uniquindio.crud.entity.program.Programa;
import co.uniquindio.crud.exception.auth.NoPermisosException;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import co.uniquindio.crud.exception.user.UsuarioNotFoundException;
import co.uniquindio.crud.repository.ProgramaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ResourceOwnerValidatorImpl{


    private final SecurityUtils securityUtils;
    private final ProgramaRepository programaRepository;

    public boolean isResourceOwner(Long resourceUserId) {
        // Implementación genérica que puede ser sobrescrita
        Long currentUserId = securityUtils.getUserId()
                .orElseThrow(() -> new UsuarioNotFoundException(0L));

        if (!currentUserId.equals(resourceUserId)) {
            throw new NoPermisosException("No tiene permisos sobre este recurso");
        }
        return true;
    }


    public boolean hasSharedAccessToPrograma(Long programaId) {
        Long currentUserId = securityUtils.getUserId()
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario no autenticado"));

        Programa programa = programaRepository.findByIdOptional(programaId)
                .orElseThrow(() -> new ProgramaNotFoundException(programaId));

        // Verificar si es el autor
        if (programa.getAutor().getId().equals(currentUserId)) {
            return true;
        }

        // Verificar si está en la lista de compartidos
        boolean tieneAcceso = programa.getCompartidoConUsuarios().stream()
                .anyMatch(usuario -> usuario.getId().equals(currentUserId));

        if (!tieneAcceso) {
            throw new NoPermisosException("No tiene acceso compartido a este programa");
        }

        return true;
    }
}