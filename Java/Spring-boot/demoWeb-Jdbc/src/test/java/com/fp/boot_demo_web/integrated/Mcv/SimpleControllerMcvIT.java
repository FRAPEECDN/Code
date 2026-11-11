package com.fp.boot_demo_web.integrated.Mcv;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fp.boot_demo_web.integrated.TestcontainersConfiguration;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.google.gson.Gson;

/** Exercises controller CRUD and paging routes with MockMvc and PostgreSQL. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class SimpleControllerMcvIT {

    private static final String PATH = "/api/simple";

    private final Gson gson = new Gson();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    /** Recreates the test records before each endpoint test. */
    @BeforeEach
    void setupBaseData() {
        jdbcTemplate.update("DELETE FROM simple");
        jdbcTemplate.update("INSERT INTO simple (name) VALUES (?)", "apple");
        jdbcTemplate.update("INSERT INTO simple (name) VALUES (?)", "banana");
        jdbcTemplate.update("INSERT INTO simple (name) VALUES (?)", "grape");
    }

    @Test
    void getsAllRecords() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(3)));
    }

    @Test
    void getsRecordById() throws Exception {
        int id = findId("banana");

        mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("banana")));
    }

    @Test
    void returnsNotFoundForMissingRecord() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", Integer.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchesRecordsWithPaging() throws Exception {
        mockMvc.perform(get(PATH + "/search").param("page", "0").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", equalTo(2)))
                .andExpect(jsonPath("$.totalElements", equalTo(3)));
    }

    @Test
    void searchesAllRecordsByDefault() throws Exception {
        mockMvc.perform(get(PATH + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", equalTo(3)))
                .andExpect(jsonPath("$.totalElements", equalTo(3)));
    }

    @Test
    void createsRecordAndReadsItBack() throws Exception {
        String request = gson.toJson(new SimpleDto(0, "coconut"));
        String response = mockMvc.perform(post(PATH)
                .contentType("application/json")
                .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo("coconut")))
                .andReturn().getResponse().getContentAsString();

        SimpleDto saved = gson.fromJson(response, SimpleDto.class);
        mockMvc.perform(get(PATH + "/{id}", saved.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("coconut")));
    }

    @Test
    void updatesRecord() throws Exception {
        int id = findId("banana");

        mockMvc.perform(put(PATH + "/{id}", id)
                .contentType("application/json")
                .content(gson.toJson(new SimpleDto(id, "coconut"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("coconut")));

        mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("coconut")));
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingRecord() throws Exception {
        mockMvc.perform(put(PATH + "/{id}", Integer.MAX_VALUE)
                .contentType("application/json")
                .content(gson.toJson(new SimpleDto(Integer.MAX_VALUE, "coconut"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchesRecord() throws Exception {
        int id = findId("banana");

        mockMvc.perform(patch(PATH + "/{id}", id)
                .contentType("application/json")
                .content(gson.toJson(new SimpleDto(id, "coconut"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("coconut")));
    }

    @Test
    void returnsNotFoundWhenPatchingMissingRecord() throws Exception {
        mockMvc.perform(patch(PATH + "/{id}", Integer.MAX_VALUE)
                .contentType("application/json")
                .content(gson.toJson(new SimpleDto(Integer.MAX_VALUE, "coconut"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletesRecord() throws Exception {
        int id = findId("banana");

        mockMvc.perform(delete(PATH + "/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundWhenDeletingMissingRecord() throws Exception {
        mockMvc.perform(delete(PATH + "/{id}", Integer.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    private int findId(String name) {
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM simple WHERE name = ?", Integer.class, name);
        return id;
    }
}
