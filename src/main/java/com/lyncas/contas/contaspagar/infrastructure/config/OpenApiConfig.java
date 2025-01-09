package com.lyncas.contas.contaspagar.infrastructure.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contasAPagarOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Contas a pagar API")
                        .version("v1")
                        .description("API para gerenciamento de contas a pagar")
                        .contact(new Contact()
                                .name("Lyncas"))
                ).addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .paths(new Paths());
    }

}
