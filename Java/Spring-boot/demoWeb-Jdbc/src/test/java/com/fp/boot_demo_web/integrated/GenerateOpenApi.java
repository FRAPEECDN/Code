package com.fp.boot_demo_web.integrated;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/** Integration utility that writes the generated OpenAPI document to disk. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class GenerateOpenApi {

    @Autowired
    private MockMvc mockMvc;

    /** Requests and saves the application's generated OpenAPI JSON. */
    @Test
    void generateOpenApiJson() throws Exception {
        String openApi = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs")
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        Path output = Path.of("src/test/openapi/openapi3.json");
        Files.createDirectories(output.getParent());
        Files.writeString(output, openApi, StandardCharsets.UTF_8);
    }
}
