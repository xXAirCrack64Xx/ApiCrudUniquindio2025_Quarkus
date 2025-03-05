package co.uniquindio.crud.repository;

import co.uniquindio.crud.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    public List<Usuario> findAllActiveUsers() {
        return list("estadoCuenta <> ?1", "ELIMINADA");
    }

    public Usuario findActiveById(Long id) {
        return find("id = ?1 and estadoCuenta <> ?2", id, "ELIMINADA").firstResult();
    }

    public Optional<Usuario> findByEmail(String email) {
        return find("email = ?1 and estadoCuenta <> ?2", email, "ELIMINADA").firstResultOptional();
    }

    public Optional<Usuario> findByCedula(String cedula) {
        return find("cedula = ?1 and estadoCuenta <> ?2", cedula, "ELIMINADA").firstResultOptional();
    }

    public List<Usuario> findActiveUsersPaged(int page, int size) {
        return find("estadoCuenta <> ?1", "ELIMINADA")
                .page(page - 1, size)
                .list();
    }

}


