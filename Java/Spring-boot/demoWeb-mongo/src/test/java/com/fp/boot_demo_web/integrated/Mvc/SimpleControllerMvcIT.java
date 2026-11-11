package com.fp.boot_demo_web.integrated.Mvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fp.boot_demo_web.integrated.TestcontainersConfiguration;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.model.entities.SimpleEntity;
import com.fp.boot_demo_web.repository.SimpleRepository;
import com.google.gson.Gson;

/** Exercises controller routes, validation, and persistence with MockMvc and MongoDB. */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class SimpleControllerMvcIT {

    private static final String PATH = "/api/simple";
    private static final Gson GSON = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SimpleRepository repository;

    @BeforeEach
    void seedDocuments() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new SimpleEntity(null, "apple"),
                new SimpleEntity(null, "banana"),
                new SimpleEntity(null, "grape")));
    }

    @Test
    void getsAllRecords() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("apple", "banana", "grape")));
    }

    @Test
    void getsRecordById() throws Exception {
        String id = findId("banana");

        mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(id)))
                .andExpect(jsonPath("$.name", equalTo("banana")));
    }

    @Test
    void returnsNotFoundForMissingRecord() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", new ObjectId().toHexString()))
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
    void searchesRecordsWithDefaultPaging() throws Exception {
        mockMvc.perform(get(PATH + "/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", equalTo(3)))
                .andExpect(jsonPath("$.totalElements", equalTo(3)));
    }

    @Test
    void createsRecordAndReadsItBack() throws Exception {
        String request = GSON.toJson(new SimpleDto("client-supplied-id", "coconut"));
        String response = mockMvc.perform(post(PATH)
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo("coconut")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        SimpleDto saved = GSON.fromJson(response, SimpleDto.class);
        assertThat(ObjectId.isValid(saved.id())).isTrue();
        mockMvc.perform(get(PATH + "/{id}", saved.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("coconut")));
        assertThat(repository.count()).isEqualTo(4);
    }

    @Test
    void updatesRecordUsingPathId() throws Exception {
        String id = findId("banana");

        mockMvc.perform(put(PATH + "/{id}", id)
                        .contentType("application/json")
                        .content(GSON.toJson(new SimpleDto("different-id", "coconut"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(id)))
                .andExpect(jsonPath("$.name", equalTo("coconut")));

        mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("coconut")));
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void returnsNotFoundWhenUpdatingMissingRecord() throws Exception {
        String id = new ObjectId().toHexString();

        mockMvc.perform(put(PATH + "/{id}", id)
                        .contentType("application/json")
                        .content(GSON.toJson(new SimpleDto(id, "coconut"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchesRecord() throws Exception {
        String id = findId("banana");

        mockMvc.perform(patch(PATH + "/{id}", id)
                        .contentType("application/json")
                        .content(GSON.toJson(new SimpleDto("different-id", "coconut"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(id)))
                .andExpect(jsonPath("$.name", equalTo("coconut")));
    }

    @Test
    void returnsNotFoundWhenPatchingMissingRecord() throws Exception {
        String id = new ObjectId().toHexString();

        mockMvc.perform(patch(PATH + "/{id}", id)
                        .contentType("application/json")
                        .content(GSON.toJson(new SimpleDto(id, "coconut"))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletesRecord() throws Exception {
        String id = findId("banana");

        mockMvc.perform(delete(PATH + "/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(PATH + "/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsNotFoundWhenDeletingMissingRecord() throws Exception {
        mockMvc.perform(delete(PATH + "/{id}", new ObjectId().toHexString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidRecordName() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType("application/json")
                        .content(GSON.toJson(new SimpleDto(null, "ab"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void servesOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists());
    }

    private String findId(String name) {
        return repository.findAll().stream()
                .filter(entity -> entity.getName().equals(name))
                .map(SimpleEntity::getId)
                .findFirst()
                .orElseThrow();
    }
}
