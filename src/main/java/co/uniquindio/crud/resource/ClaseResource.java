package co.uniquindio.crud.resource;

import co.uniquindio.crud.dto.clase.ClaseRequestDTO;
import co.uniquindio.crud.dto.clase.ClaseResponseDTO;
import co.uniquindio.crud.service.interfaces.ClaseService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;
import java.net.URI;
import java.util.List;

/**
 * Recurso JAX-RS para operaciones CRUD sobre la entidad Clase.
 * Solo recibe la petición, delega la lógica de negocio al servicio y retorna DTOs.
 */
@Path("api/v1/clases")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ClaseResource {

    private static final Logger LOGGER = Logger.getLogger(ClaseResource.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final ClaseService claseService;

    @Context
    UriInfo uriInfo;

    /**
     * Crea una nueva Clase.
     * @param request DTO con datos de creación.
     * @return Response con URI del recurso creado y DTO de salida.
     */
    @POST
    public Response crearClase(@Valid ClaseRequestDTO request) {
        LOGGER.info("Solicitud de creación de clase recibida");
        ClaseResponseDTO created = claseService.crearClase(request);
        AUDIT_LOGGER.info("Clase creada con ID=" + created.id());

        URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath())
                .path(String.valueOf(created.id()))
                .build();
        return Response.created(uri).entity(created).build();
    }

    /**
     * Obtiene todas las clases.
     * @return Lista de DTOs de Clase.
     */
    @GET
    public Response listarClases() {
        LOGGER.info("Listando todas las clases");
        return Response.ok(claseService.listarClases()).build();
    }

    /**
     * Obtiene una clase por ID.
     * @param id Identificador de la clase.
     * @return DTO de Clase.
     */
    @GET
    @Path("{id}")
    public Response obtenerClase(@PathParam("id") Long id) {
        LOGGER.infof("Buscando clase con ID=%d", id);
        ClaseResponseDTO found = claseService.obtenerClasePorId(id);
        return Response.ok(found).build();
    }

    /**
     * Actualiza una clase existente.
     * @param id Identificador de la clase a actualizar.
     * @param request DTO con nuevos datos.
     * @return Response con URI del recurso actualizado y DTO de salida.
     */
    @PUT
    @Path("{id}")
    public Response actualizarClase(@PathParam("id") Long id, @Valid ClaseRequestDTO request) {
        LOGGER.infof("Solicitud de actualización de clase ID=%d", id);
        ClaseResponseDTO updated = claseService.actualizarClase(id, request);
        AUDIT_LOGGER.info("Clase actualizada con ID=" + updated.id());

        URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath()).build();
        return Response.ok()
                .location(uri)
                .entity(updated.id())
                .build();
    }

    /**
     * Elimina una clase por ID.
     * @param id Identificador de la clase a eliminar.
     * @return Response sin contenido.
     */
    @DELETE
    @Path("{id}")
    public Response eliminarClase(@PathParam("id") Long id) {
        LOGGER.infof("Solicitud de eliminación de clase ID=%d", id);
        claseService.eliminarClase(id);
        AUDIT_LOGGER.info("Clase eliminada con ID=" + id);
        return Response.noContent().build();
    }

}

