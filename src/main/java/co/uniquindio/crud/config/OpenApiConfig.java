package co.uniquindio.crud.config;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.servers.Server;

import java.util.Arrays;

@ApplicationScoped
public class OpenApiConfig {

    @Produces
    @Priority(1)
    public OpenAPI customOpenAPI() {
        Info info = OASFactory.createInfo()
                .title("API de CRUD - Uniquindio")
                .version("1.0")
                .description("Esta API permite gestionar usuarios (estudiantes y profesores) y trabajos (envíos y calificaciones).\n\n" +
                        "Los endpoints permiten realizar operaciones CRUD sobre usuarios y gestionar el envío y calificación de trabajos.")
                .contact(OASFactory.createContact()
                        .name("Diego Alejandro Pastrana Fernandez, Santiago Fernandez, Andres Felipe Rendon y Juan Camilo Valbuena")
                        .email("diegoa.pastranaf@uqvirtual.edu.co, andresf.rendonn@uqvirtual.edu.co")
                        .url("https://www.uniquindio.edu.co/"))
                .license(OASFactory.createLicense()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));

        ExternalDocumentation externalDocs = OASFactory.createExternalDocumentation()
                .description("Repositorio GitHub")
                .url("https://github.com/alejandropastrana58152559/Api_RESTfull_Crud_Uniquindio");

        Server serverLocal = OASFactory.createServer()
                .url("http://localhost:8080/CRUD")
                .description("Servidor Local");

        Server serverProd = OASFactory.createServer()
                .url("https://api.uniquindio.edu/CRUD")
                .description("Servidor de Producción");

        return OASFactory.createOpenAPI()
                .info(info)
                .externalDocs(externalDocs)
                .servers(Arrays.asList(serverLocal, serverProd));
    }
}



