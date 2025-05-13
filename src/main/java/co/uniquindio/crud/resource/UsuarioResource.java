package co.uniquindio.crud.resource;

import co.uniquindio.crud.dto.PaginacionResponseDTO;
import co.uniquindio.crud.dto.ParcialUserUpdate;
import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.exception.ParametrosInvalidosException;
import co.uniquindio.crud.service.implementations.UsuarioServiceImplements;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger; // Cambio clave aquí
import java.net.URI;

/**
 * Recurso REST para la gestión de usuarios.
 * <p>
 * Este controlador implementa los endpoints para realizar operaciones CRUD sobre usuarios,
 * utilizando <strong>JBoss Log Manager</strong> (logging nativo de Quarkus) para el registro de logs.
 * Se emplea un flujo de logging que informa el inicio y el resultado de las operaciones sin saturar el sistema,
 * y se registra de forma separada la auditoría de eventos sensibles.
 * </p>
 */
@Path("/api/v1/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UsuarioResource {

    private static final Logger LOGGER = Logger.getLogger(UsuarioResource.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final UsuarioServiceImplements usuarioService;

    /**
     * Obtiene la información de un usuario por su identificador.
     *
     * @param userId Identificador del usuario en formato String.
     * @return Respuesta HTTP con el usuario encontrado.
     * @throws ParametrosInvalidosException Si el userId no es un número válido.
     */
    @GET
    @Path("/{userId}")
    public Response getUsuarioById(@PathParam("userId") String userId) {
        LOGGER.infov("Inicio consulta de usuario con userId: {0}", userId);
        try {
            long userIdLong = validarYConvertirParametro(userId, "userId");

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debugv("Parámetro userId convertido a número: {0}", userIdLong);
            }

            UsuarioResponseDTO responseDTO = usuarioService.getUsuarioById(userIdLong);
            LOGGER.infov("Consulta exitosa para el usuario con userId: {0}", userId);
            return Response.ok(responseDTO).build();
        }
        catch (NumberFormatException e) {
            LOGGER.errorv("userId inválido: {0}", userId);
            throw new ParametrosInvalidosException("El userId debe ser un número válido.");
        }
        catch (Exception e) {
            LOGGER.errorv("Error al consultar el usuario con userId: {0}", userId, e);
            throw e;
        }
    }

    /**
     * Obtiene la información de un usuario por su email.
     *
     * @param email Identificador del usuario en formato String.
     * @return Respuesta HTTP con el usuario encontrado.
     */

    @GET
    @Path("/email/{email}")
    public Response getUsuarioByEmail(@PathParam("email") String email) {
        LOGGER.infov("Inicio consulta de usuario con email: {0}", email);
            UsuarioResponseDTO responseDTO = usuarioService.findbyemail(email);
            LOGGER.infov("Consulta exitosa para el usuario con email: {0}", email);
            return Response.ok(responseDTO).build();
    }

    /**
     * Obtiene la lista paginada de usuarios.
     *
     * @param pageStr Parámetro de página en formato String.
     * @param sizeStr Parámetro del tamaño de página en formato String.
     * @return Respuesta HTTP con la lista de usuarios paginada.
     * @throws ParametrosInvalidosException Si los parámetros no son numéricos o son menores que 1.
     */
    @GET
    public Response getAllUsuarios(
            @QueryParam("page") @DefaultValue("1") String pageStr,
            @QueryParam("size") @DefaultValue("50") String sizeStr
    ) {
        LOGGER.infov("Inicio consulta de usuarios paginados. Página: {0}, Tamaño: {1}", pageStr, sizeStr);
        try {
            int page = validarYConvertirParametro(pageStr, "página");
            int size = validarYConvertirParametro(sizeStr, "tamaño de página");

            if (page < 1 || size < 1) {
                LOGGER.warnv("Parámetros de paginación inválidos: página {0} o tamaño {1}", page, size);
                throw new ParametrosInvalidosException("Los parámetros de paginación deben ser mayores que 0");
            }

            PaginacionResponseDTO response = usuarioService.getAllUsuariosPaginados(page, size);
            LOGGER.infov("Consulta paginada completada para página {0} con tamaño {1}", page, size);
            return Response.ok(response).build();
        }
        catch (NumberFormatException e) {
            LOGGER.errorv("Error en formato numérico: {0}", e.getMessage());
            throw new ParametrosInvalidosException("Los parámetros de paginación deben ser números válidos");
        }
        catch (ParametrosInvalidosException e) {
            LOGGER.warnv("Parámetros inválidos: {0}", e.getMessage());
            throw e;
        }
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param usuarioDTO Objeto con la información del usuario a crear.
     * @param uriInfo    Contexto de la URI para construir la ubicación del recurso.
     * @return Respuesta HTTP con la ubicación y datos del usuario creado.
     */
    @POST
    public Response createUsuario(@Valid UsuarioDTO usuarioDTO, @Context UriInfo uriInfo) {
        LOGGER.infov("Inicio creación de usuario con email: {0}", usuarioDTO.email());
        try {
            UsuarioResponseDTO responseDTO = usuarioService.createUsuario(usuarioDTO);
            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(responseDTO.id()));
            URI location = builder.build();

            LOGGER.infov("Usuario creado exitosamente con userId: {0}", responseDTO.id());
            AUDIT_LOGGER.infov("AUDIT: Creación de usuario | ID: {0} | Email: {1}", responseDTO.id(), usuarioDTO.email());
            return Response.created(location).entity(responseDTO).build();
        }
        catch (Exception e) {
            LOGGER.errorv("Error al crear el usuario con email: {0}", usuarioDTO.email(), e);
            throw e;
        }
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param userIdStr  Identificador del usuario en formato String.
     * @param usuarioDTO Objeto con los nuevos datos del usuario.
     * @return Respuesta HTTP con la información actualizada del usuario.
     * @throws ParametrosInvalidosException Si el userId no es un número válido.
     */
    @PUT
    @Path("/{userId}")
    public Response updateUsuario(
            @PathParam("userId") String userIdStr,
            @Valid UsuarioDTO usuarioDTO
    ) {
        LOGGER.infov("Inicio actualización de usuario con userId: {0}", userIdStr);
        try {
            long userId = validarYConvertirParametro(userIdStr, "userId");
            UsuarioResponseDTO responseDTO = usuarioService.updateUsuario(userId, usuarioDTO);

            LOGGER.infov("Usuario actualizado exitosamente con userId: {0}", userId);
            AUDIT_LOGGER.infov("AUDIT: Actualización de usuario | ID: {0} | Email: {1}", userId, usuarioDTO.email());
            return Response.ok(responseDTO).build();
        }
        catch (NumberFormatException e) {
            LOGGER.errorv("userId inválido: {0}", userIdStr);
            throw new ParametrosInvalidosException("El userId debe ser un número válido.");
        }
        catch (Exception e) {
            LOGGER.errorv("Error al actualizar el usuario con userId: {0}", userIdStr, e);
            throw e;
        }
    }

    /**
     * Realiza una actualización parcial de un usuario.
     *
     * @param userIdStr  Identificador del usuario en formato String.
     * @param usuarioDTO Objeto con los datos a actualizar.
     * @return Respuesta HTTP con la información actualizada del usuario.
     * @throws ParametrosInvalidosException Si el userId no es un número válido.
     */
    @PATCH
    @Path("/{userId}")
    public Response partialUpdateUsuario(
            @PathParam("userId") String userIdStr,
            @Valid ParcialUserUpdate usuarioDTO
    ) {
        LOGGER.infov("Inicio actualización parcial para usuario con userId: {0}", userIdStr);
        try {
            long userId = validarYConvertirParametro(userIdStr, "userId");
            UsuarioResponseDTO response = usuarioService.partialUpdateUsuario(userId, usuarioDTO);

            LOGGER.infov("Actualización parcial completada para usuario con userId: {0}", userId);
            AUDIT_LOGGER.infov("AUDIT: Actualización parcial | ID: {0} | Email: {1}", userId, usuarioDTO.email());
            return Response.ok(response).build();
        }
        catch (NumberFormatException e) {
            LOGGER.errorv("userId inválido: {0}", userIdStr);
            throw new ParametrosInvalidosException("El userId debe ser un número válido");
        }
    }

    /**
     * Elimina un usuario existente.
     *
     * @param userIdStr Identificador del usuario en formato String.
     * @return Respuesta HTTP sin contenido si la eliminación fue exitosa.
     * @throws ParametrosInvalidosException Si el userId no es un número válido.
     */
    @DELETE
    @Path("/{userId}")
    public Response deleteUsuario(@PathParam("userId") String userIdStr) {
        LOGGER.infov("Inicio eliminación de usuario con userId: {0}", userIdStr);
        try {
            long userId = validarYConvertirParametro(userIdStr, "userId");
            usuarioService.deleteUsuario(userId);

            LOGGER.infov("Usuario eliminado exitosamente con userId: {0}", userId);
            AUDIT_LOGGER.infov("AUDIT: Eliminación de usuario | ID: {0}", userId);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
        catch (NumberFormatException e) {
            LOGGER.errorv("userId inválido: {0}", userIdStr);
            throw new ParametrosInvalidosException("El userId debe ser un número válido.");
        }
        catch (Exception e) {
            LOGGER.errorv("Error al eliminar el usuario con userId: {0}", userIdStr, e);
            throw e;
        }
    }

    /**
     * Valida y convierte un parámetro de tipo String a entero.
     *
     * @param valor           Valor a convertir.
     * @param nombreParametro Nombre descriptivo del parámetro.
     * @return El valor convertido a entero.
     * @throws ParametrosInvalidosException Si la conversión falla.
     */
    private int validarYConvertirParametro(String valor, String nombreParametro) {
        try {
            int numero = Integer.parseInt(valor);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debugv("Conversión exitosa del parámetro {0}: {1}", nombreParametro, numero);
            }
            return numero;
        }
        catch (NumberFormatException e) {
            LOGGER.errorv("Fallo en la conversión del parámetro {0} con valor: {1}", nombreParametro, valor);
            throw new ParametrosInvalidosException(
                    String.format("Parámetro '%s' con valor '%s' no es un número válido", nombreParametro, valor)
            );
        }
    }



}