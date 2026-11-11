package com.frapee.securitydemo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configures the OpenAPI schema and Swagger security definition.
 * <p>
 * The API is documented with a reusable HTTP Basic security scheme named
 * {@code basicAuth}, which is then required for all endpoints described by the
 * generated OpenAPI document.
 */
@Configuration
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "basicAuth", scheme = "basic")
@OpenAPIDefinition(info = @Info(title = "Security API", version = "v1"), security = @SecurityRequirement(name = "basicAuth"))
public class ConfigOpenAPI {

}
