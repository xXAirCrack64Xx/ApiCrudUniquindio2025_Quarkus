package co.uniquindio.crud.repository;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import co.uniquindio.crud.entity.program.Programa;

import java.util.List;

/**
 * Repository for managing Programa entities.
 * Provides CRUD operations and custom query capabilities via Panache.
 */
@ApplicationScoped
public class ProgramaRepository implements PanacheRepository<Programa> {

    /**
     * Busca todos los programas creados por un autor específico.
     * @param autorId ID del usuario autor.
     * @return Lista de programas.
     */
    public List<Programa> findByAutorId(Long autorId) {
        return list("autor.id", autorId);
    }

    /**
     * Busca todos los programas compartidos con un usuario específico.
     * @param usuarioId ID del usuario.
     * @return Lista de programas compartidos con el usuario.
     */
    public List<Programa> findSharedWithUsuario(Long usuarioId) {
        return list("compartidoConUsuarios.id", usuarioId);
    }

    /**
     * Busca todos los programas compartidos con una clase específica.
     * @param claseId ID de la clase.
     * @return Lista de programas compartidos con la clase.
     */
    public List<Programa> findSharedWithClase(Long claseId) {
        return list("compartidoConClases.id", claseId);
    }

}

