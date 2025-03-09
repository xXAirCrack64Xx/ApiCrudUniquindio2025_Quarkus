package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.PaginacionResponseDTO;
import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.entity.EstadoCuenta;
import co.uniquindio.crud.entity.OcupacionUsuario;
import co.uniquindio.crud.entity.Usuario;
import co.uniquindio.crud.exception.NoUsuariosRegistradosException;
import co.uniquindio.crud.exception.UsuarioNotFoundException;
import co.uniquindio.crud.exception.UsuarioYaExisteException;
import co.uniquindio.crud.repository.UsuarioRepository;
import co.uniquindio.crud.service.mappers.UsuarioMapper;
import co.uniquindio.crud.service.interfaces.UsuarioService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UsuarioServiceImplements implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponseDTO getUsuarioById(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            log.warn("Usuario no encontrado con ID: {}", id);
            throw new UsuarioNotFoundException(id);
        }
        log.info("Usuario encontrado: {}", usuario);
        return usuarioMapper.toResponseDTO(usuario);
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
                .map(usuarioMapper::toResponseDTO)
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

        // Convertir de DTO a entidad usando el mapper
        Usuario nuevoUsuario = usuarioMapper.toEntity(usuarioDTO);

        // Guardar el usuario en la base de datos
        usuarioRepository.persist(nuevoUsuario);
        log.info("Usuario creado exitosamente con ID: {}", nuevoUsuario.getId());

        // Mapear a DTO de respuesta
        return usuarioMapper.toResponseDTO(nuevoUsuario);
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

        // Actualizar los datos del usuario usando el mapper
        usuarioMapper.updateEntityFromDTO(usuarioDTO, usuario);

        // Guardar los cambios
        usuarioRepository.persist(usuario);
        log.info("Usuario actualizado exitosamente con ID: {}", usuario.getId());

        // Mapear a DTO de respuesta
        return usuarioMapper.toResponseDTO(usuario);
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

        return usuarioMapper.toResponseDTO(usuario);
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

}
