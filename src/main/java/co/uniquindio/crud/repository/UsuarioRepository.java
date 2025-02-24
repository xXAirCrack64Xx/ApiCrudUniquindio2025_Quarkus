package co.uniquindio.crud.repository;

import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import co.uniquindio.crud.entity.Usuario;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    // Puedes agregar métodos de búsqueda adicionales si lo requieres
}

