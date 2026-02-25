package project.Justina.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Justina API",
                version = "v1.0.0",
                description = "API RESTful para la Plataforma de Telemetría Quirúrgica Justina. Esta aplicación proporciona servicios para el monitoreo y análisis de datos quirúrgicos en tiempo real.",
                contact = @Contact(
                        name = "Equipo 24 - No Country",
                        email = "equipo24@nocoountry.tech",
                        url = "https://github.com/no-country-simulation"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Servidor de Desarrollo Local"
                ),
                @Server(
                        url = "https://s02-26-equipo-24-web-app-development.onrender.com",
                        description = "Servidor de Producción"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth"),
                @SecurityRequirement(name = "apiKey")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT de autenticación. Obtenga el token mediante el endpoint /api/v1/auth/login"
)
@SecurityScheme(
        name = "apiKey",
        type = SecuritySchemeType.APIKEY,
        in = io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER,
        paramName = "X-API-KEY",
        description = "API Key para acceso de servicios externos (IA)"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                                .description("Token JWT de autenticación"))
                        .addSecuritySchemes("apiKey", new io.swagger.v3.oas.models.security.SecurityScheme()
                                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.APIKEY)
                                .in(io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER)
                                .name("X-API-KEY")
                                .description("API Key para servicios externos")));
    }
}
