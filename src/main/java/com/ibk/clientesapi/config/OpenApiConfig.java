package com.ibk.clientesapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clientesApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Clientes API - IBK")
                        .description("API reactiva para gestión de clientes con trazabilidad asíncrona hacia Azure Event Hubs")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Interbank")
                                .email("soporte@ibk.com.pe")));
    }
}
