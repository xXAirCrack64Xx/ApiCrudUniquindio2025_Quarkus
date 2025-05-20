package co.uniquindio.crud.service.implementations;

import co.uniquindio.crud.dto.user.PaginacionUserResponseDTO;
import co.uniquindio.crud.dto.user.ParcialUserUpdate;
import co.uniquindio.crud.dto.user.UsuarioDTO;
import co.uniquindio.crud.dto.user.UsuarioResponseDTO;
import co.uniquindio.crud.entity.user.EstadoCuenta;
import co.uniquindio.crud.entity.user.Usuario;
import co.uniquindio.crud.exception.user.NoUsuariosRegistradosException;
import co.uniquindio.crud.exception.user.UsuarioNotFoundException;
import co.uniquindio.crud.exception.user.UsuarioYaExisteException;
import co.uniquindio.crud.repository.UsuarioRepository;
import co.uniquindio.crud.service.emailService.EmailService;
import co.uniquindio.crud.service.mappers.UsuarioMapper;
import co.uniquindio.crud.service.interfaces.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger; // Cambio clave aquí

/**
 * Servicio para la gestión de usuarios.
 * <p>
 * Implementa las operaciones CRUD para usuarios, gestionando la validación, conversión y persistencia de datos.
 * Se utiliza <strong>JBoss Logging</strong> (logging nativo de Quarkus) para el registro de logs generales
 * y auditoría de operaciones sensibles.
 * </p>
 */
@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UsuarioServiceImplements implements UsuarioService {

    private static final Logger LOGGER = Logger.getLogger(UsuarioServiceImplements.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    private final EmailService emailService;

    /**
     * Obtiene un usuario activo por su identificador.
     *
     * @param id Identificador del usuario.
     * @return DTO con la información del usuario.
     * @throws UsuarioNotFoundException Si no se encuentra el usuario.
     */
    @Override
    public UsuarioResponseDTO getUsuarioById(Long id) {
        LOGGER.infov("Buscando usuario con ID: {0}", id);
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            LOGGER.warnv("Usuario no encontrado con ID: {0}", id);
            throw new UsuarioNotFoundException(id);
        }
        LOGGER.infov("Usuario encontrado: {0}", usuario);
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Obtiene la lista de usuarios activos paginada.
     *
     * @param page Número de página.
     * @param size Tamaño de la página.
     * @return DTO con la información de paginación y lista de usuarios.
     * @throws NoUsuariosRegistradosException Si no hay usuarios registrados.
     */
    @Override
    @RolesAllowed("PROFESOR")
    public PaginacionUserResponseDTO getAllUsuariosPaginados(int page, int size) {
        LOGGER.infov("Obteniendo usuarios paginados - Página: {0}, Tamaño: {1}", page, size);

        List<Usuario> usuarios = usuarioRepository.findActiveUsersPaged(page, size);
        long totalUsuarios = usuarioRepository.count();
        int totalPaginas = (int) Math.ceil((double) totalUsuarios / size);

        if (totalUsuarios == 0) { // Mejor condición para validar realmente sin usuarios
            LOGGER.warn("No hay usuarios registrados en el sistema");
            throw new NoUsuariosRegistradosException("No hay usuarios registrados en el sistema");
        }

        List<UsuarioResponseDTO> usuariosDTO = usuarios.stream()
                .map(usuarioMapper::toResponseDTO).toList();

        PaginacionUserResponseDTO response = new PaginacionUserResponseDTO(usuariosDTO, page, totalPaginas, totalUsuarios, size);
        LOGGER.infov("Paginación exitosa - Total usuarios: {0}, Páginas: {1}", totalUsuarios, totalPaginas);
        return response;
    }

    /**
     * Crea un nuevo usuario validando que el correo y la cédula no existan previamente.
     *
     * @param usuarioDTO Datos del usuario a crear.
     * @return DTO con la información del usuario creado.
     * @throws UsuarioYaExisteException Si ya existe un usuario con el correo o la cédula especificada.
     */
    @Transactional
    @Override
    public UsuarioResponseDTO createUsuario(UsuarioDTO usuarioDTO) {
        LOGGER.infov("Creando usuario con email: {0} y cédula: {1}", usuarioDTO.email(), usuarioDTO.cedula());

        if (usuarioRepository.findByEmail(usuarioDTO.email()).isPresent()) {
            LOGGER.warnv("Ya existe un usuario con el correo: {0}", usuarioDTO.email());
            throw new UsuarioYaExisteException("Correo " + usuarioDTO.email() + " ya registrado");
        }

        if (usuarioRepository.findByCedula(usuarioDTO.cedula()).isPresent()) {
            LOGGER.warnv("Ya existe un usuario con la cédula: {0}", usuarioDTO.cedula());
            throw new UsuarioYaExisteException("Cédula " + usuarioDTO.cedula() + " ya registrada");
        }

        Usuario nuevoUsuario = usuarioMapper.toEntity(usuarioDTO);
        usuarioRepository.persist(nuevoUsuario);
        LOGGER.infov("Usuario creado exitosamente con ID: {0}", nuevoUsuario.getId());
        AUDIT_LOGGER.infov("AUDIT: Creación | ID: {0} | Email: {1}", nuevoUsuario.getId(), usuarioDTO.email());

        // apartado de envío del correo
        emailService.enviarCorreo(nuevoUsuario.getEmail(), "Su usuario ha sido creado con éxito");


        return usuarioMapper.toResponseDTO(nuevoUsuario);
    }

    /**
     * Actualiza un usuario activo existente.
     *
     * @param id         Identificador del usuario a actualizar.
     * @param usuarioDTO Datos nuevos para el usuario.
     * @return DTO con la información actualizada del usuario.
     * @throws UsuarioNotFoundException Si no se encuentra el usuario.
     * @throws UsuarioYaExisteException Si el nuevo correo ya está registrado en otro usuario.
     */
    @Transactional
    @Override
    public UsuarioResponseDTO updateUsuario(Long id, UsuarioDTO usuarioDTO) {
        LOGGER.infov("Actualizando usuario con ID: {0}", id);

        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            LOGGER.warnv("Usuario no encontrado con ID: {0}", id);
            throw new UsuarioNotFoundException(id);
        }

        if (!usuario.getEmail().equals(usuarioDTO.email()) &&
                usuarioRepository.findByEmail(usuarioDTO.email()).isPresent()) {
            LOGGER.warnv("Correo ya registrado: {0}", usuarioDTO.email());
            throw new UsuarioYaExisteException("Correo " + usuarioDTO.email() + " ya existe");
        }

        usuarioMapper.updateEntityFromDTO(usuarioDTO, usuario);
        usuarioRepository.persist(usuario);
        LOGGER.infov("Usuario actualizado exitosamente con ID: {0}", usuario.getId());
        AUDIT_LOGGER.infov("AUDIT: Actualización | ID: {0} | Email: {1}", usuario.getId(), usuarioDTO.email());
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Realiza una actualización parcial de un usuario activo.
     *
     * @param id  Identificador del usuario.
     * @param dto Datos parciales a actualizar.
     * @return DTO con la información actualizada del usuario.
     * @throws UsuarioNotFoundException Si no se encuentra el usuario.
     * @throws UsuarioYaExisteException Si se intenta actualizar con un correo o cédula ya registrados.
     */
    @Transactional
    @Override
    public UsuarioResponseDTO partialUpdateUsuario(Long id, ParcialUserUpdate dto) {
        LOGGER.infov("Inicio actualización parcial para usuario con ID: {0}", id);
        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            LOGGER.warnv("Usuario no encontrado con ID: {0}", id);
            throw new UsuarioNotFoundException(id);
        }

        boolean needsUpdate = false;

        if (dto.nombre() != null && !dto.nombre().equals(usuario.getNombre())) {
            usuario.setNombre(dto.nombre());
            needsUpdate = true;
            LOGGER.debugv("Actualizando nombre a: {0}", dto.nombre());
        }

        if (dto.cedula() != null && !usuario.getCedula().equals(dto.cedula())) {
            usuarioRepository.findByCedula(dto.cedula()).ifPresent(existing -> {
                LOGGER.warnv("Cédula duplicada: {0}", dto.cedula());
                throw new UsuarioYaExisteException("Cédula " + dto.cedula() + " ya registrada");
            });
            usuario.setCedula(dto.cedula());
            needsUpdate = true;
        }

        if (dto.email() != null && !usuario.getEmail().equals(dto.email())) {
            usuarioRepository.findByEmail(dto.email()).ifPresent(existing -> {
                LOGGER.warnv("Correo duplicado: {0}", dto.email());
                throw new UsuarioYaExisteException("Correo " + dto.email() + " ya registrado");
            });
            usuario.setEmail(dto.email());
            needsUpdate = true;
        }

        if (needsUpdate) {
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.persist(usuario);
            LOGGER.infov("Usuario ID: {0} actualizado parcialmente", id);
            AUDIT_LOGGER.infov("AUDIT: Actualización parcial | ID: {0} | Email: {1}", id, dto.email());
        } else {
            LOGGER.infov("Sin cambios detectados para usuario ID: {0}", id);
        }
        return usuarioMapper.toResponseDTO(usuario);
    }

    /**
     * Marca a un usuario activo como eliminado cambiando su estado.
     *
     * @param id Identificador del usuario a eliminar.
     * @throws UsuarioNotFoundException Si no se encuentra el usuario.
     */
    @Transactional
    @Override
    public void deleteUsuario(Long id) {
        LOGGER.infov("Eliminando usuario con ID: {0}", id);

        Usuario usuario = usuarioRepository.findActiveById(id);
        if (usuario == null) {
            LOGGER.warnv("Usuario no encontrado con ID: {0}", id);
            throw new UsuarioNotFoundException(id);
        }
        usuario.setEstadoCuenta(EstadoCuenta.ELIMINADA);
        usuarioRepository.persist(usuario);
        LOGGER.infov("Usuario ID: {0} eliminado exitosamente", id);
        AUDIT_LOGGER.infov("AUDIT: Eliminación | ID: {0}", id);
    }
    // hola

    @Override
    public UsuarioResponseDTO findbyemail (String email){
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() -> new UsuarioNotFoundException(email));
        return usuarioMapper.toResponseDTO(usuario);
    }
}