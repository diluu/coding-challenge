package com.thudani.codingchallenge.util;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI springdocOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Coding Challenge API")
                        .description("Coding Challenge battery save and search api")
                        .version("v0.0.1"))
                .externalDocs(new ExternalDocumentation()
                        .description("Github repository")
                        .url("https://github.com/diluu/coding-challenge"));
    }
}
