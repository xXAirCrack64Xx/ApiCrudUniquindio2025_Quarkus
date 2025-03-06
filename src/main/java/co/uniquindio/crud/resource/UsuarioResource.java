package co.uniquindio.crud.resource;

import co.uniquindio.crud.dto.PaginacionResponseDTO;
import co.uniquindio.crud.exception.ParametrosInvalidosException;
import co.uniquindio.crud.service.implementations.UsuarioServiceImplemets;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.net.URI;

import co.uniquindio.crud.dto.UsuarioDTO;
import co.uniquindio.crud.dto.UsuarioResponseDTO;
import co.uniquindio.crud.dto.ErrorResponse;

@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuarios", description = "Endpoints para gestionar usuarios: creación, consulta, actualización y eliminación.")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class UsuarioResource {

    private final UsuarioServiceImplemets usuarioService;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Obtener un usuario por ID",
            description = "Recupera la información detallada de un usuario en base a su ID. " +
                    "El ID debe ser un número válido. Si el usuario no existe, se devolverá un error 404."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Usuario encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"id\": 1, \"nombre\": \"Juan Pérez\", \"email\": \"juan.perez@example.com\", \"clase\": \"Premium\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 400, \"mensaje\": \"El ID debe ser un número válido\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}"
                            )
                    )
            )
    })
    public Response getUsuarioById(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "ID del usuario a consultar. Debe ser un número válido.",
                    required = true,
                    schema = @Schema(implementation = Long.class),
                    examples = @ExampleObject(value = "1")
            )
            @PathParam("id") String id) {
        log.info("Iniciando consulta de usuario con ID: {}", id);
        try {
            long idUser = validarYConvertirParametro(id, "Id");
            UsuarioResponseDTO responseDTO = usuarioService.getUsuarioById(idUser);
            log.info("Consulta exitosa para el usuario con ID: {}", id);
            return Response.ok(responseDTO).build();
        }catch (NumberFormatException e){
            log.error("ID inválido: {}", id);
            throw new ParametrosInvalidosException("El ID debe ser un número válido.");
        }
        catch (Exception e) {
            log.error("Error al consultar el usuario con ID: {}", id, e);
            throw e; // El manejador de excepciones global se encargará de esto
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Obtener usuarios paginados",
            description = "Recupera una lista paginada de usuarios con metadatos de paginación. " +
                    "Parámetros deben ser números enteros positivos."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Usuarios encontrados exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PaginacionResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "usuarios": [
                    {
                      "id": 1,
                      "nombre": "Juan Pérez",
                      "email": "juan.perez@example.com",
                      "clase": "Premium"
                    }
                  ],
                  "paginaActual": 1,
                  "totalPaginas": 5,
                  "totalUsuarios": 42,
                  "resultadosPorPagina": 50
                }"""
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "204",
                    description = "No hay usuarios registrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 204, \"mensaje\": \"No hay usuarios registrados en el sistema.\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Parámetros inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Valor no numérico",
                                            value = "{\"codigo\": 400, \"mensaje\": \"Los parámetros de paginación deben ser números válidos.\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Valores negativos",
                                            value = "{\"codigo\": 400, \"mensaje\": \"Los parámetros de paginación deben ser mayores que 0.\"}"
                                    )
                            }
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}"
                            )
                    )
            )
    })
    public Response getAllUsuarios(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Número de página (debe ser ≥ 1)",
                    schema = @Schema(implementation = Integer.class, defaultValue = "1", minimum = "1")
            )
            @QueryParam("page") @DefaultValue("1") String pageStr,

            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "Resultados por página (debe ser ≥ 1)",
                    schema = @Schema(implementation = Integer.class, defaultValue = "50", minimum = "1")
            )
            @QueryParam("size") @DefaultValue("50") String sizeStr) {

        try {
            // Validación y conversión de parámetros
            int page = validarYConvertirParametro(pageStr, "página");
            int size = validarYConvertirParametro(sizeStr, "tamaño de página");

            // Validar valores positivos
            if (page < 1 || size < 1) {
                throw new ParametrosInvalidosException("Los parámetros de paginación deben ser mayores que 0");
            }

            PaginacionResponseDTO response = usuarioService.getAllUsuariosPaginados(page, size);
            return Response.ok(response).build();

        } catch (NumberFormatException e) {
            log.error("Error en formato numérico: {}", e.getMessage());
            throw new ParametrosInvalidosException("Los parámetros de paginación deben ser números válidos");
        } catch (ParametrosInvalidosException e) {
            log.warn("Parámetros inválidos: {}", e.getMessage());
            throw e;
        }
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Crear un nuevo usuario",
            description = "Crea un nuevo usuario con los datos proporcionados. La contraseña se encripta antes de guardarla."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "201",
                    description = "Usuario creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                      "id": 1,
                      "nombre": "Juan Pérez",
                      "email": "juan.perez@example.com",
                      "clase": "Premium",
                      "fechaCreacion": "2023-10-05T12:34:56"
                    }"""
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "Conflicto: Ya existe un usuario con el mismo correo electrónico o cédula",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 409, \"mensaje\": \"Ya existe un usuario registrado con el correo juan.perez@example.com\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}"
                            )
                    )
            )
    })
    public Response createUsuario(
            @Parameter(
                    description = "Datos del usuario a crear",
                    required = true,
                    schema = @Schema(implementation = UsuarioDTO.class))
                    @Valid UsuarioDTO usuarioDTO,
                    @Context UriInfo uriInfo) {

        log.info("Iniciando creación de usuario con email: {}", usuarioDTO.email());
        try {
            UsuarioResponseDTO responseDTO = usuarioService.createUsuario(usuarioDTO);

            // Construir la URL del recurso creado
            UriBuilder builder = uriInfo.getAbsolutePathBuilder().path(Long.toString(responseDTO.id()));
            URI location = builder.build();

            log.info("Usuario creado exitosamente con ID: {}", responseDTO.id());
            return Response.created(location).entity(responseDTO).build();
        } catch (Exception e) {
            log.error("Error al crear el usuario", e);
            throw e; // El manejador de excepciones global se encargará de esto
        }
    }


    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Actualizar un usuario",
            description = "Actualiza los datos de un usuario existente. El ID debe ser un número válido."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                    {
                      "id": 1,
                      "nombre": "Juan Pérez",
                      "email": "juan.perez@example.com",
                      "clase": "Premium",
                      "fechaCreacion": "2023-10-05T12:34:56",
                      "fechaActualizacion": "2023-10-06T14:20:00"
                    }"""
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 400, \"mensaje\": \"El ID debe ser un número válido.\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "Conflicto: Ya existe un usuario con el mismo correo electrónico",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 409, \"mensaje\": \"Ya existe un usuario con el correo juan.perez@example.com\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}"
                            )
                    )
            )
    })
    public Response updateUsuario(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "ID del usuario a actualizar",
                    required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
                    @PathParam("id") String idStr,
                    @Parameter(
                            description = "Datos del usuario a actualizar",
                            required = true,
                            schema = @Schema(implementation = UsuarioDTO.class))
                    @Valid UsuarioDTO usuarioDTO) {

        log.info("Iniciando actualización de usuario con ID: {}", idStr);
        try {
            // Validar y convertir el ID
            long id = validarYConvertirParametro(idStr, "Id");

            // Actualizar el usuario
            UsuarioResponseDTO responseDTO = usuarioService.updateUsuario(id, usuarioDTO);
            log.info("Usuario actualizado exitosamente con ID: {}", id);
            return Response.ok(responseDTO).build();
        } catch (NumberFormatException e) {
            log.error("ID inválido: {}", idStr);
            throw new ParametrosInvalidosException("El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al actualizar el usuario", e);
            throw e; // El manejador de excepciones global se encargará de esto
        }
    }


    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Actualización parcial de usuario",
            description = "Actualiza campos específicos de un usuario existente. Solo los campos proporcionados serán modificados. La contraseña no se puede actualizar con este método."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Usuario actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                {
                  "id": 1,
                  "nombre": "Juan Pérez Actualizado",
                  "email": "nuevo.email@example.com",
                  "clase": "Premium",
                  "fechaCreacion": "2023-10-05T12:34:56",
                  "fechaActualizacion": "2023-10-06T15:45:00"
                }"""
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "ID inválido o parámetros incorrectos",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 400, \"mensaje\": \"El ID debe ser un número válido\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "409",
                    description = "Conflicto: Correo electrónico ya en uso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 409, \"mensaje\": \"El correo nuevo.email@example.com ya está registrado\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor\"}"
                            )
                    )
            )
    })
    public Response partialUpdateUsuario(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "ID del usuario a actualizar",
                    required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
                    @PathParam("id") String idStr,
                    @Parameter(
                            description = "Campos a actualizar en formato JSON",
                            required = true,
                            schema = @Schema(implementation = UsuarioDTO.class))
                    @Valid UsuarioDTO usuarioDTO) {

        log.info("Iniciando actualización parcial para usuario ID: {}", idStr);
        try {
            long id = validarYConvertirParametro(idStr, "Id");
            UsuarioResponseDTO response = usuarioService.partialUpdateUsuario(id, usuarioDTO);
            return Response.ok(response).build();
        } catch (NumberFormatException e) {
            log.error("ID inválido: {}", idStr);
            throw new ParametrosInvalidosException("El ID debe ser un número válido");
        } catch (Exception e) {
            log.error("Error en actualización parcial: {}", e.getMessage());
            throw e;
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Eliminar un usuario",
            description = "Elimina un usuario existente. El ID debe ser un número válido."
    )
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "204",
                    description = "Usuario eliminado exitosamente"
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "ID inválido",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 400, \"mensaje\": \"El ID debe ser un número válido.\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 404, \"mensaje\": \"Usuario no encontrado con ID: 99\"}"
                            )
                    )
            ),
            @APIResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = "{\"codigo\": 500, \"mensaje\": \"Error interno del servidor. Inténtelo nuevamente más tarde.\"}"
                            )
                    )
            )
    })
    public Response deleteUsuario(
            @Parameter(
                    in = ParameterIn.PATH,
                    description = "ID del usuario a eliminar",
                    required = true,
                    schema = @Schema(implementation = Long.class, example = "1"))
                    @PathParam("id") String idStr) {

        log.info("Iniciando eliminación de usuario con ID: {}", idStr);
        try {
            // Validar y convertir el ID
            long id = validarYConvertirParametro(idStr, "Id");

            // Eliminar el usuario
            usuarioService.deleteUsuario(id);
            log.info("Usuario eliminado exitosamente con ID: {}", id);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (NumberFormatException e) {
            log.error("ID inválido: {}", idStr);
            throw new ParametrosInvalidosException("El ID debe ser un número válido.");
        } catch (Exception e) {
            log.error("Error al eliminar el usuario", e);
            throw e; // El manejador de excepciones global se encargará de esto
        }
    }

    private int validarYConvertirParametro(String valor, String nombreParametro) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new ParametrosInvalidosException(
                    String.format("Parámetro '%s' con valor '%s' no es un número válido", nombreParametro, valor)
            );
        }
    }

}
