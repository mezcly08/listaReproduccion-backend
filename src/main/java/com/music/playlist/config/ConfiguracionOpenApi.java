package com.music.playlist.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionOpenApi {

    @Bean
    public OpenAPI configuracionOpenApiPersonalizada() {
        final String nombreEsquemaSeguridad = "basicAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API REST de Gestión de Listas de Reproducción")
                        .version("1.0.0")
                        .description("API REST para la administración de listas de reproducción de música y sus canciones asociadas.")
                        .contact(new Contact()
                                .name("Equipo de Arquitectura Backend")
                                .email("backend@music.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io/")))
                .addSecurityItem(new SecurityRequirement().addList(nombreEsquemaSeguridad))
                .components(new Components()
                        .addSecuritySchemes(nombreEsquemaSeguridad, new SecurityScheme()
                                .name(nombreEsquemaSeguridad)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Autenticación HTTP Basic. Ingrese usuario y contraseña (user:user123 o admin:admin123)")));
    }
}
