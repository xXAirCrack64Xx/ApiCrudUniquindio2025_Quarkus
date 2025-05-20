package co.uniquindio.crud.resource;

import co.uniquindio.crud.dto.ejemplo.CompartirConClasesRequest;
import co.uniquindio.crud.dto.ejemplo.ProgramaEjemploRequestDto;
import co.uniquindio.crud.dto.ejemplo.ProgramaEjemploResponse;
import co.uniquindio.crud.dto.program.PagedResponse;
import co.uniquindio.crud.dto.program.ProgramaResponseDTO;
import co.uniquindio.crud.service.interfaces.EjemploService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;

@Path("api/v1/ejemplos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ProgramaEjemploResource {


    private static final Logger LOGGER = Logger.getLogger(ProgramaEjemploResource.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final EjemploService ejemploService;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("/tema/{tema}")
    public Response obtenerEjemplosPorTema(
            @PathParam("tema") String tema) {

        LOGGER.infof("Buscando ejemplos de tema: %s", tema);
        List<ProgramaEjemploResponse> ejemplos = ejemploService.obtenerEjemplosPorTema(tema);
        return Response.ok(ejemplos).build();
    }
    

    /**
     * Crea un nuevo ejemplo (solo profesores).
     */
    @POST
    public Response crearEjemplo(@Valid ProgramaEjemploRequestDto request) {
        LOGGER.info("Solicitud de creación de ejemplo recibida");
        ProgramaEjemploResponse created = ejemploService.crearPrograma(request);
        AUDIT_LOGGER.infof("Ejemplo creado con ID=%d", created.id());
        URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath())
                .path(String.valueOf(created.id()))
                .build();
        return Response.created(uri).entity(created).build();
    }

    /**
     * Lista ejemplos con filtros (tema, dificultad, etc.).
     */
    @GET
    public Response listarEjemplos(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("tema") String tema,
            @QueryParam("dificultad") String dificultad) {

        LOGGER.infof("Listando ejemplos - página %d, tamaño %d", page, size);
        PagedResponse<ProgramaEjemploResponse> paged = ejemploService.listarEjemplos(
                page, size, tema, dificultad
        );
        return Response.ok(paged).build();
    }

    /**
     * Obtiene un ejemplo por ID.
     */
    @GET
    @Path("{id}")
    public Response obtenerEjemplo(@PathParam("id") Long id) {
        LOGGER.infof("Obteniendo ejemplo con ID=%d", id);
        ProgramaEjemploResponse dto = ejemploService.obtenerEjemploPorId(id);
        return Response.ok(dto).build();
    }

    /**
     * Actualiza un ejemplo (solo autor profesor).
     */
    @PUT
    @Path("{id}")
    public Response actualizarEjemplo(@PathParam("id") Long id,
                                      @Valid ProgramaEjemploRequestDto request) {
        LOGGER.infof("Actualizando ejemplo ID=%d", id);
        ProgramaEjemploResponse updated = ejemploService.actualizarEjemplo(id, request);
        AUDIT_LOGGER.infof("Ejemplo actualizado ID=%d", id);
        return Response.ok(updated).build();
    }

    /**
     * Elimina un ejemplo (solo autor profesor).
     */
    @DELETE
    @Path("{id}")
    public Response eliminarEjemplo(@PathParam("id") Long id) {
        LOGGER.infof("Eliminando ejemplo ID=%d", id);
        ejemploService.eliminarEjemplo(id);
        AUDIT_LOGGER.infof("Ejemplo eliminado ID=%d", id);
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{idPrograma}/clases")
    public Response compartirConClases(
            @PathParam("idPrograma") Long idPrograma,
            @Valid CompartirConClasesRequest request) {

        ProgramaEjemploResponse response = ejemploService.compartirConClases(idPrograma, request);
        return Response.ok(response).build();
    }

}
