package com.example.naijaWallet.config;

import com.example.naijaWallet.exception.ErrorResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("https://mwallet-production.up.railway.app")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                        .addResponses("401", errorResponse("Unauthorized"))
                        .addResponses("400", errorResponse("Bad Request"))
                        .addResponses("404", errorResponse("Not Found"))
                        .addResponses("429", errorResponse("Too Many Requests"))
                        .addResponses("409", errorResponse("Already Exists"))
                        .addResponses("403", errorResponse("Forbidden"))
                        .addResponses("503", errorResponse("Service Unavailable"))
                        .addResponses("500", errorResponse("Internal Server Error"))
                );
    }


    private ApiResponse errorResponse(String description) {
        return new ApiResponse()
                .description(description)
                .content(new Content().addMediaType(
                        "application/json",
                        new MediaType().schema(
                                new Schema<>().$ref("#/components/schemas/ErrorResponse")
                        )
                ));
    }
}
