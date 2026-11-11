package com.frapee.securitydemo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

/**
 * Configures the OpenAPI schema and Swagger security definition.
 * <p>
 * The API is documented with the session cookie used after a successful
 * password and TOTP login.
 */
@Configuration
@SecurityScheme(type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.COOKIE, name = "sessionCookie",
		paramName = "JSESSIONID")
@OpenAPIDefinition(info = @Info(title = "TOTP Security API", version = "v1"), security = @SecurityRequirement(name = "sessionCookie"))
public class ConfigOpenAPI {

}
