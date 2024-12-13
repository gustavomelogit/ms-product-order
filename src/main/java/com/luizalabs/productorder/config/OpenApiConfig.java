package com.luizalabs.productorder.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OpenApiConfig {

    @Value("${app.version}")
    String applicationVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Application version: {}", applicationVersion);

        return new OpenAPI()
                .components(new Components())
                .info( new Info()
                    .title("MS Product Order")
                    .description("Microservice for migrating order products. Created to allow the import and consultation of products related to orders placed by customers.")
                    .version(applicationVersion)
                );
    }
}
