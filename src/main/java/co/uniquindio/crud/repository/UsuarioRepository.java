package co.uniquindio.crud.repository;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import co.uniquindio.crud.entity.Usuario;

import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    // Buscar un usuario por email
    public Optional<Usuario> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<Usuario> findByCedula(String cedula) {
        return find("cedula", cedula).firstResultOptional();
    }
    // Puedes agregar métodos de búsqueda adicionales si lo requieres
}

