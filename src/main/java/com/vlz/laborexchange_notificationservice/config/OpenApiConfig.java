package com.vlz.laborexchange_notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .version("1.0.0")
                        .description("""
                                Handles real-time notifications and transactional emails.

                                **REST endpoint:**
                                - `GET /api/notifications/stream` — SSE stream for real-time notifications (requires JWT via `?token=` query param, since EventSource API cannot send Authorization headers)

                                **Kafka topics consumed:**
                                - `notification-new-application` — notifies employer of new job application
                                - `notification-accepted-application` — notifies candidate that application was accepted
                                - `notification-rejected-application` — notifies candidate that application was rejected
                                - `notification-withdrawn-application` — notifies employer that application was withdrawn
                                - `email-verification` — sends email verification link
                                - `password-reset-email` — sends password reset link

                                All email listeners route failures to Dead Letter Topics (DLT).
                                """)
                        .license(new License().name("MIT")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtained via POST /api/auth/login")))
                .servers(List.of(
                        new Server().url("http://localhost:8088").description("Direct"),
                        new Server().url("http://localhost:8080").description("Via API Gateway")))
                .tags(List.of(
                        new Tag().name("Notifications").description("SSE stream for real-time push notifications")));
    }
}
