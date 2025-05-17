package co.uniquindio.crud.resource;

import co.uniquindio.crud.dto.program.ExecutionResponseDTO;
import co.uniquindio.crud.service.interfaces.ProgramaExecutionService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@Path("api/v1/programas/{id}/ejecutar")
@Produces(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class ExecutionResource {

    private static final Logger LOGGER = Logger.getLogger(ExecutionResource.class);
    private static final Logger AUDIT_LOGGER = Logger.getLogger("audit");

    private final ProgramaExecutionService executionService;

    @POST
    public Response ejecutar(@PathParam("id") Long id) {
        LOGGER.info("Ejecutando el programa con id");
        ExecutionResponseDTO result = executionService.ejecutarPrograma(id);
        return Response.ok(result).build();
    }

}
