package co.uniquindio.crud.repository;

import co.uniquindio.crud.entity.EstadoCuenta;
import co.uniquindio.crud.entity.Usuario;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {

    public List<Usuario> findAllActiveUsers() {
        return list("estadoCuenta <> ?1", EstadoCuenta.ELIMINADA);
    }

    public Usuario findActiveById(Long id) {
        return find("id = ?1 and estadoCuenta <> ?2", id, EstadoCuenta.ELIMINADA).firstResult();
    }

    public Optional<Usuario> findByEmail(String email) {
        return find("email = ?1 and estadoCuenta <> ?2", email, EstadoCuenta.ELIMINADA).firstResultOptional();
    }

    public Optional<Usuario> findByCedula(String cedula) {
        return find("cedula = ?1 and estadoCuenta <> ?2", cedula, EstadoCuenta.ELIMINADA).firstResultOptional();
    }

    public List<Usuario> findActiveUsersPaged(int page, int size) {
        return find("estadoCuenta <> ?1", EstadoCuenta.ELIMINADA)
                .page(page - 1, size)
                .list();
    }

}


