package com.fp.boot_demo_web.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Defines the title and version shown in the generated OpenAPI specification.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Demo Web Mongo API", version = "v1"))
public class OpenApiConfig {

}
