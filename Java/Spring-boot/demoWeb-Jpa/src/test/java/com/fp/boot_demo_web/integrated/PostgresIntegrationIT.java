package com.fp.boot_demo_web.integrated;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.google.gson.Gson;

/** Exercises REST and persistence behavior against a real PostgreSQL container. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class PostgresIntegrationIT {

    private final Gson gson = new Gson();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    /** Clears records so each integration test starts with an empty table. */
    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.update("DELETE FROM simple");
    }

    /** Creates a record over HTTP and verifies it was persisted in PostgreSQL. */
    @Test
    void createsRecordAndReadsItBackFromPostgres() throws Exception {
        String name = "test-" + UUID.randomUUID();
        String request = gson.toJson(new SimpleDto(0, name));

        MvcResult createResult = mockMvc.perform(post("/api/simple")
                .contentType("application/json")
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo(name)))
                .andReturn();

        SimpleDto saved = gson.fromJson(createResult.getResponse().getContentAsString(), SimpleDto.class);
        assertTrue(saved.id() > 0);

        mockMvc.perform(get("/api/simple/{id}", saved.id()))
                .andExpect(status().isOk())
                .andExpect(content().json(gson.toJson(saved)));

        assertEquals(
                name,
                jdbcTemplate.queryForObject("SELECT name FROM simple WHERE id = ?", String.class, saved.id()));
    }

    /** Confirms a request for a missing record returns HTTP 404. */
    @Test
    void returnsNotFoundForMissingRecord() throws Exception {
        mockMvc.perform(get("/api/simple/{id}", Integer.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    /** Confirms the generated OpenAPI document is available over HTTP. */
    @Test
    void servesOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists());
    }
}
