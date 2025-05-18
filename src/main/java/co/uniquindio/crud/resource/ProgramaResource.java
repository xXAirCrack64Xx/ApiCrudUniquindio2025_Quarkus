package co.uniquindio.crud.resource;

import co.uniquindio.crud.dto.comment.ComentarioRequestDTO;
import co.uniquindio.crud.dto.comment.ComentarioResponseDTO;
import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.dto.program.ProgramaRequestDTO;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import co.uniquindio.crud.exception.program.ProgramaNotFoundException;
import co.uniquindio.crud.service.interfaces.ProgramaService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
 * Recurso JAX-RS para CRUD de Programa.
 * Solo recibe peticiones, delega la lógica al servicio y retorna DTOs.
 */
@Path("api/v1/programas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProgramaResource {

    private static final Logger LOGGER = Logger.getLogger(ProgramaResource.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");
    private final ProgramaService programaService;

    @Context
    UriInfo uriInfo;

    /**
     * Crea un nuevo programa.
     * @param request DTO con datos para creación.
     * @return Response con URI del recurso creado y DTO de salida.
     */
    @POST
    public Response crearPrograma(@Valid ProgramaRequestDTO request) {
        LOGGER.info("Solicitud de creación de programa recibida");
        ProgramaResponseDTO created = programaService.crearPrograma(request);
        AUDIT_LOGGER.infof("Programa creado con ID=%d", created.id());
        URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath())
                .path(String.valueOf(created.id()))
                .build();
        return Response.created(uri).entity(created).build();
    }

    /**
     * Obtiene todos los programas con paginación.
     * @param page número de página (1-based).
     * @param size tamaño de página.
     * @return PagedResponse con lista de DTOs y metadatos.
     */
    @GET
    public Response listarProgramas(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        LOGGER.infof("Listando programas - página %d, tamaño %d", page, size);
        PagedResponse<ProgramaResponseDTO> paged = programaService.listarProgramas(page, size);
        return Response.ok(paged).build();
    }

    /**
     * Obtiene un programa por ID.
     * @param id identificador.
     * @return DTO de Programa.
     */
    @GET
    @Path("{id}")
    public Response obtenerPrograma(@PathParam("id") Long id) {
        LOGGER.infof("Obteniendo programa con ID=%d", id);
        ProgramaResponseDTO dto = programaService.obtenerProgramaPorId(id);
        return Response.ok(dto).build();
    }

    /**
     * Actualiza un programa existente.
     * @param id identificador.
     * @param request DTO con datos nuevos.
     * @return Response con URI del recurso modificado y DTO actualizado.
     */
    @PUT
    @Path("{id}")
    public Response actualizarPrograma(@PathParam("id") Long id,
                                       @Valid ProgramaRequestDTO request) {
        LOGGER.infof("Solicitud de actualización de programa ID=%d", id);
        ProgramaResponseDTO updated = programaService.actualizarPrograma(id, request);
        AUDIT_LOGGER.infof("Programa actualizado con ID=%d", id);
        URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath()).build();
        return Response.ok()
                .location(uri)
                .entity(updated)
                .build();
    }

    /**
     * Elimina un programa por ID.
     * @param id identificador.
     * @return Response sin contenido.
     */
    @DELETE
    @Path("{id}")
    public Response eliminarPrograma(@PathParam("id") Long id) {
        LOGGER.infof("Solicitud de eliminación de programa ID=%d", id);
        programaService.eliminarPrograma(id);
        AUDIT_LOGGER.infof("Programa eliminado con ID=%d", id);
        return Response.noContent().build();
    }

    /**
     * Califica un programa con una nota entre 0 y 5.
     * @param id ID del programa.
     * @param nota Nueva nota.
     * @return mensaje de confirmación.
     */
    @PATCH
    @Path("{id}/calificaciones")
    public Response calificarPrograma(@PathParam("id") Long id,
                                      @QueryParam("nota") @Min(0) @Max(5) Long nota) {
        LOGGER.infof("Solicitud para calificar programa ID=%d con nota=%d", id, nota);
        String resultado = programaService.calificarPrograma(id, nota);
        AUDIT_LOGGER.infof("Programa ID=%d calificado con nota=%d", id, nota);
        return Response.ok(resultado).build();
    }


    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @Path("{idPrograma}/comentarios")
    public Response comentarPrograma(
            @PathParam("idPrograma") Long idPrograma,
            @Valid ComentarioRequestDTO request) {

        LOGGER.infof("Comentando programa ID=%d por profesor ID=%d", idPrograma, request.idProfesor());

        ComentarioResponseDTO resultado = programaService.comentarPrograma(idPrograma, request);
        AUDIT_LOGGER.infof("Comentario actualizado programa ID=%d", idPrograma);
        return Response.ok(resultado).build();
    }


}



