package com.turkcell.blogging_platform.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI bloggingPlatformOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blogging Platform API")
                        .description("API Documentation for Blogging Platform")
                        .version("1.0"))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"));
    }
}