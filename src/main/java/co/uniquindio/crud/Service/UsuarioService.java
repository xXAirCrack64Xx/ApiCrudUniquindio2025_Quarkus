package co.uniquindio.crud.Service;

import co.uniquindio.crud.dto.PaginacionResponseDTO;
import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.entity.EstadoCuenta;
import co.uniquindio.crud.entity.OcupacionUsuario;
import co.uniquindio.crud.entity.RolUsuario;
import co.uniquindio.crud.entity.Usuario;
import co.uniquindio.crud.exception.NoUsuariosRegistradosException;
import co.uniquindio.crud.exception.UsuarioNotFoundException;
import co.uniquindio.crud.exception.UsuarioYaExisteException;
import co.uniquindio.crud.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioResponseDTO getUsuarioById(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            log.warn("Usuario no encontrado con ID: {}", id);
            throw new UsuarioNotFoundException(id);
        }
        log.info("Usuario encontrado: {}", usuario);
        return mapToDTO(usuario);
    }

    public PaginacionResponseDTO getAllUsuariosPaginados(int page, int size) {
        log.info("Obteniendo usuarios paginados - Página: {}, Tamaño: {}", page, size);

        // Obtener usuarios paginados
        List<Usuario> usuarios = usuarioRepository.findActiveUsersPaged(page, size);

        // Obtener conteo total de usuarios
        long totalUsuarios = usuarioRepository.count();
        int totalPaginas = (int) Math.ceil((double) totalUsuarios / size);

        if (usuarios.isEmpty()) {
            log.warn("No hay usuarios registrados en el sistema");
            throw new NoUsuariosRegistradosException("No hay usuarios registrados en el sistema");
        }

        // Mapear a DTO
        List<UsuarioResponseDTO> usuariosDTO = usuarios.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        // Construir respuesta de paginación
        PaginacionResponseDTO response = new PaginacionResponseDTO(usuariosDTO, page, totalPaginas, totalUsuarios, size);
        log.info("Paginación exitosa - Total usuarios: {}, Páginas: {}", totalUsuarios, totalPaginas);
        return response;
    }

    @Transactional
    public UsuarioResponseDTO createUsuario(UsuarioDTO usuarioDTO) {
        log.info("Creando usuario con email: {} y cédula: {}", usuarioDTO.email(), usuarioDTO.cedula());

        // Validar que el email no exista
        if (usuarioRepository.find("email", usuarioDTO.email()).firstResult() != null) {
            log.warn("Ya existe un usuario registrado con el correo: {}", usuarioDTO.email());
            throw new UsuarioYaExisteException("Ya existe un usuario registrado con el correo " + usuarioDTO.email());
        }

        // Validar que la cédula no exista
        if (usuarioRepository.find("cedula", usuarioDTO.cedula()).firstResult() != null) {
            log.warn("Ya existe un usuario registrado con la cédula: {}", usuarioDTO.cedula());
            throw new UsuarioYaExisteException("Ya existe un usuario registrado con la cédula " + usuarioDTO.cedula());
        }

        // Encriptar la contraseña
        String claveEncriptada = BCrypt.hashpw(usuarioDTO.clave(), BCrypt.gensalt());

        // Crear el nuevo usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(usuarioDTO.nombre());
        nuevoUsuario.setCedula(usuarioDTO.cedula());
        nuevoUsuario.setEmail(usuarioDTO.email());
        nuevoUsuario.setOcupacion(OcupacionUsuario.valueOf(usuarioDTO.ocupacion()));
        nuevoUsuario.setRol(RolUsuario.GUEST);
        nuevoUsuario.setEstadoCuenta(EstadoCuenta.REGISTRADA);
        nuevoUsuario.setClase(usuarioDTO.clase());
        nuevoUsuario.setClave(claveEncriptada);
        nuevoUsuario.setFechaCreacion(LocalDateTime.now()); // Fecha de creación

        // Guardar el usuario en la base de datos
        usuarioRepository.persist(nuevoUsuario);
        log.info("Usuario creado exitosamente con ID: {}", nuevoUsuario.getId());

        // Mapear a DTO de respuesta
        return mapToDTO(nuevoUsuario);
    }

    @Transactional
    public UsuarioResponseDTO updateUsuario(Long id, UsuarioDTO usuarioDTO) {
        log.info("Actualizando usuario con ID: {}", id);

        // Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            log.warn("Usuario no encontrado con ID: {}", id);
            throw new UsuarioNotFoundException(id);
        }

        // Validar que el nuevo email no esté en uso por otro usuario
        if (!usuario.getEmail().equals(usuarioDTO.email()) &&
                usuarioRepository.find("email", usuarioDTO.email()).firstResult() != null) {
            log.warn("Ya existe un usuario con el correo: {}", usuarioDTO.email());
            throw new UsuarioYaExisteException("Ya existe un usuario con el correo " + usuarioDTO.email());
        }

        // Actualizar los datos del usuario
        usuario.setNombre(usuarioDTO.nombre());
        usuario.setCedula(usuarioDTO.cedula());
        usuario.setEmail(usuarioDTO.email());
        usuario.setOcupacion(OcupacionUsuario.valueOf(usuarioDTO.ocupacion()));
        usuario.setClase(usuarioDTO.clase());
        usuario.setFechaActualizacion(LocalDateTime.now()); // Fecha de actualización

        // Guardar los cambios
        usuarioRepository.persist(usuario);
        log.info("Usuario actualizado exitosamente con ID: {}", usuario.getId());

        // Mapear a DTO de respuesta
        return mapToDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO partialUpdateUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) throw new UsuarioNotFoundException(id);

        boolean needsUpdate = false;

        if (dto.nombre() != null) {
            usuario.setNombre(dto.nombre());
            needsUpdate = true;
        }

        if (dto.cedula() != null && !usuario.getCedula().equals(dto.cedula())) {
            if (usuarioRepository.findByCedula(dto.cedula()).isPresent()) {
                throw new UsuarioYaExisteException("La cedula " + dto.cedula() + " ya está registrada");
            }
            usuario.setCedula(dto.cedula());
            needsUpdate = true;
        }

        if (dto.email() != null && !usuario.getEmail().equals(dto.email())) {
            if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
                throw new UsuarioYaExisteException("El correo " + dto.email() + " ya está registrado");
            }
            usuario.setEmail(dto.email());
            needsUpdate = true;
        }

        if (dto.ocupacion() != null) {
            usuario.setOcupacion(OcupacionUsuario.valueOf(dto.ocupacion()));
            needsUpdate = true;
        }

        if (dto.clase() != null) {
            usuario.setClase(dto.clase());
            needsUpdate = true;
        }

        if (needsUpdate) {
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.persist(usuario);
            log.info("Usuario ID: {} actualizado parcialmente", id);
        }

        return mapToDTO(usuario);
    }

    @Transactional
    public void deleteUsuario(Long id) {
        log.info("Eliminando usuario con ID: {}", id);

        // Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            log.warn("Usuario no encontrado con ID: {}", id);
            throw new UsuarioNotFoundException(id);
        }
        usuario.setEstadoCuenta(EstadoCuenta.ELIMINADA);
        // Eliminar el usuario
        usuarioRepository.persist(usuario);
        log.info("Usuario eliminado exitosamente con ID: {}", id);
    }

    private UsuarioResponseDTO mapToDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNombre(),
                usuario.getEmail(),
                usuario.getClase(),
                usuario.getFechaCreacion(),
                usuario.getFechaActualizacion(),
                usuario.getEstadoCuenta())
                ;
    }

}
