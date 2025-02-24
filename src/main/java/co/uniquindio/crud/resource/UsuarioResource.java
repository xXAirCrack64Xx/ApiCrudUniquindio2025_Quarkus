package co.uniquindio.crud.resource;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

import co.uniquindio.crud.entity.Usuario;
import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.repository.UsuarioRepository;
import co.uniquindio.crud.exception.ErrorResponse;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Endpoints para gestionar usuarios: creación, consulta, actualización y eliminación.")
public class UsuarioResource {

    @Inject
    UsuarioRepository usuarioRepository;

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener un usuario por ID", description = "Recupera la información de un usuario dado su ID.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @APIResponse(responseCode = "400", description = "ID inválido (no es un número válido)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 400, \"mensaje\": \"El ID debe ser un número válido\"}"))),
            @APIResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}")))
    })
    public Response getUsuarioById(
            @Parameter(in = ParameterIn.PATH, description = "ID del usuario a consultar", required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
            @PathParam("id") Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(404, "Usuario no encontrado con ID: " + id))
                        .build();
            }
            UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
            responseDTO.id = usuario.id;
            responseDTO.nombre = usuario.nombre;
            responseDTO.email = usuario.email;
            responseDTO.clase = usuario.clase;
            return Response.ok(responseDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Error interno del servidor. Inténtelo nuevamente más tarde."))
                    .build();
        }
    }

    @GET
    @Operation(summary = "Listar todos los usuarios", description = "Devuelve una lista de todos los usuarios registrados en el sistema con páginacion.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO[].class))),
            @APIResponse(responseCode = "204", description = "No hay usuarios registrados",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 204, \"mensaje\": \"No hay usuarios registrados en el sistema.\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}")))
    })
    public Response getAllUsuarios(
            @Parameter(in = ParameterIn.QUERY, description = "Número de página",
                    schema = @Schema(implementation = Integer.class, defaultValue = "1", example = "1"))
            @QueryParam("page") int page,
            @Parameter(in = ParameterIn.QUERY, description = "Número de resultados por página",
                    schema = @Schema(implementation = Integer.class, defaultValue = "10", example = "50"))
            @QueryParam("size") int size) {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll().page(page - 1, size).list();
            if (usuarios.isEmpty()) {
                return Response.status(204)
                        .entity(new ErrorResponse(204, "No hay usuarios registrados en el sistema."))
                        .build();
            }
            List<UsuarioResponseDTO> responseList = usuarios.stream().map(usuario -> {
                UsuarioResponseDTO dto = new UsuarioResponseDTO();
                dto.id = usuario.id;
                dto.nombre = usuario.nombre;
                dto.email = usuario.email;
                dto.clase = usuario.clase;
                return dto;
            }).collect(Collectors.toList());
            return Response.ok(responseList).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Error interno del servidor. Inténtelo nuevamente más tarde."))
                    .build();
        }
    }

    @POST
    @Transactional
    @Operation(summary = "Crear un nuevo usuario", description = "Crea un usuario nuevo en el sistema. El usuario debe proporcionar información válida y cumplir con las restricciones establecidas.")
    @APIResponses(value = {
            @APIResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @APIResponse(responseCode = "400", description = "Solicitud inválida (datos incorrectos o incompletos)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 400, \"mensaje\": \"El campo 'cédula' es obligatorio\"}"))),
            @APIResponse(responseCode = "409", description = "Conflicto (usuario con cédula o correo ya registrado)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 409, \"mensaje\": \"Ya existe un usuario registrado con el correo juan.perez@uniquindio.edu\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}")))
    })
    public Response createUsuario(
            @Parameter(description = "Datos del usuario a crear", required = true, schema = @Schema(implementation = UsuarioDTO.class))
            @Valid UsuarioDTO usuarioDTO,
            @jakarta.ws.rs.core.Context UriInfo uriInfo) {
        try {
            if (usuarioRepository.find("email", usuarioDTO.email).firstResult() != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse(409, "Ya existe un usuario registrado con el correo " + usuarioDTO.email))
                        .build();
            }
            Usuario nuevo = new Usuario();
            nuevo.nombre = usuarioDTO.nombre;
            nuevo.cedula = usuarioDTO.cedula;
            nuevo.email = usuarioDTO.email;
            nuevo.rol = usuarioDTO.rol;
            nuevo.clase = usuarioDTO.clase;
            nuevo.clave = usuarioDTO.clave;

            usuarioRepository.persist(nuevo);

            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(nuevo.id));
            UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
            responseDTO.id = nuevo.id;
            responseDTO.nombre = nuevo.nombre;
            responseDTO.email = nuevo.email;
            responseDTO.clase = nuevo.clase;

            return Response.created(builder.build()).entity(responseDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Error interno del servidor. Inténtelo nuevamente más tarde."))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Actualizar un usuario existente", description = "Modifica los datos de un usuario registrado en el sistema dado su ID. Se debe proporcionar un objeto JSON con los nuevos valores.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @APIResponse(responseCode = "400", description = "Solicitud inválida (datos incorrectos o incompletos)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 400, \"mensaje\": \"El campo 'email' no es válido\"}"))),
            @APIResponse(responseCode = "409", description = "Conflicto (usuario con cédula o correo ya registrado)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 409, \"mensaje\": \"Ya existe un usuario con el correo juan.perez@uniquindio.edu\"}"))),
            @APIResponse(responseCode = "404", description = "Usuario no encontrado con el ID especificado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 500, \"mensaje\": \"Error interno al actualizar el usuario\"}")))
    })
    public Response updateUsuario(
            @Parameter(in = ParameterIn.PATH, description = "ID del usuario a actualizar", required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
            @PathParam("id") Long id,
            @Parameter(description = "Datos del usuario a actualizar", required = true, schema = @Schema(implementation = UsuarioDTO.class))
            @Valid UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = usuarioRepository.findById(id);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(404, "Usuario no encontrado con ID: " + id))
                        .build();
            }
            if (!usuario.email.equals(usuarioDTO.email) && usuarioRepository.find("email", usuarioDTO.email).firstResult() != null) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorResponse(409, "Ya existe un usuario con el correo " + usuarioDTO.email))
                        .build();
            }
            usuario.nombre = usuarioDTO.nombre;
            usuario.cedula = usuarioDTO.cedula;
            usuario.email = usuarioDTO.email;
            usuario.rol = usuarioDTO.rol;
            usuario.clase = usuarioDTO.clase;
            usuario.clave = usuarioDTO.clave;

            UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
            responseDTO.id = usuario.id;
            responseDTO.nombre = usuario.nombre;
            responseDTO.email = usuario.email;
            responseDTO.clase = usuario.clase;

            return Response.ok(responseDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Error interno al actualizar el usuario"))
                    .build();
        }
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Actualizar parcialmente un usuario", description = "Permite actualizar uno o varios campos de un usuario existente dado su ID. Solo los campos proporcionados en el cuerpo de la solicitud serán modificados.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Usuario actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(value = "{\"nombre\": \"Nuevo Nombre\", \"email\": \"nuevo.email@example.com\", \"rol\": \"ESTUDIANTE\", \"clase\": \"Programación II\"}"))),
            @APIResponse(responseCode = "404", description = "Usuario no encontrado con el ID especificado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"))),
            @APIResponse(responseCode = "409", description = "Conflicto al actualizar el usuario",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 409, \"mensaje\": \"Correo ya está en uso por otro usuario.\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 500, \"mensaje\": \"Error interno al actualizar el usuario\"}")))
    })
    public Response partialUpdateUsuario(
            @Parameter(in = ParameterIn.PATH, description = "ID del usuario a actualizar", required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
            @PathParam("id") Long id,
            @Parameter(description = "Campos a actualizar en formato JSON. Solo los campos enviados serán modificados.", required = true)
            UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = usuarioRepository.findById(id);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(404, "Usuario no encontrado con ID: " + id))
                        .build();
            }
            if (usuarioDTO.nombre != null)
                usuario.nombre = usuarioDTO.nombre;
            if (usuarioDTO.cedula != null)
                usuario.cedula = usuarioDTO.cedula;
            if (usuarioDTO.email != null) {
                if (!usuario.email.equals(usuarioDTO.email) && usuarioRepository.find("email", usuarioDTO.email).firstResult() != null) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity(new ErrorResponse(409, "Correo ya está en uso por otro usuario."))
                            .build();
                }
                usuario.email = usuarioDTO.email;
            }
            if (usuarioDTO.rol != null)
                usuario.rol = usuarioDTO.rol;
            if (usuarioDTO.clase != null)
                usuario.clase = usuarioDTO.clase;
            if (usuarioDTO.clave != null)
                usuario.clave = usuarioDTO.clave;

            UsuarioResponseDTO responseDTO = new UsuarioResponseDTO();
            responseDTO.id = usuario.id;
            responseDTO.nombre = usuario.nombre;
            responseDTO.email = usuario.email;
            responseDTO.clase = usuario.clase;

            return Response.ok(responseDTO).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Error interno al actualizar el usuario"))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario del sistema dado su ID. Si el usuario no existe, devuelve un error 404.")
    @APIResponses(value = {
            @APIResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @APIResponse(responseCode = "404", description = "Usuario no encontrado con el ID especificado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"))),
            @APIResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"codigo\": 500, \"mensaje\": \"Error interno al intentar eliminar el usuario\"}")))
    })
    public Response deleteUsuario(
            @Parameter(in = ParameterIn.PATH, description = "ID del usuario a eliminar", required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
            @PathParam("id") Long id) {
        try {
            Usuario usuario = usuarioRepository.findById(id);
            if (usuario == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(new ErrorResponse(404, "Usuario no encontrado con ID: " + id))
                        .build();
            }
            usuarioRepository.delete(usuario);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(500, "Error interno al intentar eliminar el usuario"))
                    .build();
        }
    }
}
