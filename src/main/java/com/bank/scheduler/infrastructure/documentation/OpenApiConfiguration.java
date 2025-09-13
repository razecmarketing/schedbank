package com.bank.scheduler.infrastructure.documentation;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI schedBankOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("SchedBank Transfer Scheduler API")
                .description("Enterprise-grade financial transfer scheduling system with comprehensive security, performance optimization, and monitoring capabilities.")
                .version("v1.0.0")
                .contact(new Contact()
                    .name("SchedBank Engineering Team")
                    .email("engineering@schedbank.com")
                    .url("https://github.com/razecmarketing/schedbank")
                )
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")
                )
            )
            .servers(List.of(
                new Server()
                    .url("https://api.schedbank.com/v1")
                    .description("Production Server"),
                new Server()
                    .url("https://staging-api.schedbank.com/v1")
                    .description("Staging Server"),
                new Server()
                    .url("http://localhost:8080/v1")
                    .description("Development Server")
            ))
            .addSecurityItem(new SecurityRequirement()
                .addList("Bearer Authentication")
            )
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token for API authentication")
                )
            );
    }
}