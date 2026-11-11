package com.frapee.JwtSecurityDemo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configures the Swagger/OpenAPI metadata for the JWT demo application.
 *
 * <p>The security scheme exposes a bearer-token authentication definition so the
 * generated API docs match the runtime authorization model.</p>
 */
@Configuration
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "Bearer Authentication", bearerFormat = "JWT", scheme = "bearer")
@OpenAPIDefinition(info = @Info(title = "JWT Security API", version = "v1"), security = @SecurityRequirement(name = "Bearer Authentication") )
public class ConfigOpenApi {

    /**
     * Creates the OpenAPI configuration.
     */
    public ConfigOpenApi() {
    }

}
