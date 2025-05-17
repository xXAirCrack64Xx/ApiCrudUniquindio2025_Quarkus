package co.uniquindio.crud.repository;

import co.uniquindio.crud.entity.clase.ClaseStatus;
import co.uniquindio.crud.entity.user.EstadoCuenta;
import co.uniquindio.crud.entity.user.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import co.uniquindio.crud.entity.clase.Clase;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing Clase entities.
 * Provides CRUD operations and query capabilities via Panache.
 */
@ApplicationScoped
public class ClaseRepository implements PanacheRepository<Clase> {

    public List<Clase> findByProfesorId(Long profesorId) {
         return list("profesor.id", profesorId);
    }

    public List<Clase> findByEstudianteId(Long estudianteId) {
         return list("estudiantes.id", estudianteId);
    }

    public Optional<Clase> findClaseById(Long id) {
        return find("id = ?1 and claseStatus <> ?2", id, ClaseStatus.DELETED).firstResultOptional();
    }

    public List<Clase> findAllClases() {
        return find("claseStatus <> ?1", ClaseStatus.DELETED).list();
    }
}

