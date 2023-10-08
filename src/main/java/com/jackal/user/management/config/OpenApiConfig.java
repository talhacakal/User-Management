package com.jackal.user.management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Talha Çakal",
                        email = "muhammedtalhacakal@gmail.com",
                        url = "linkedin.com/in/talhacakal"
                ),
                description = "OpenApi documentation for User Management",
                title = "User Management",
                version = "1.0"
        ),
        servers = @Server(
                description = "Local Environment",
                url = "http://localhost:8080"
        )
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT auth",
        scheme = "bearer",
        bearerFormat = "JWT",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.COOKIE
)
public class OpenApiConfig {
}
