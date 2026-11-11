package com.fp.boot_demo_web.integrated;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.model.entities.SimpleEntity;
import com.fp.boot_demo_web.repository.SimpleRepository;
import com.google.gson.Gson;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

/** Exercises the web API and MongoDB document mapping against an in-memory MongoDB server. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MongoIntegrationTest {

    private static final String DATABASE = "demo_web_mongo_test";
    private static final MongoServer MONGO_SERVER = new MongoServer(new MemoryBackend());
    private static final String MONGO_URI = MONGO_SERVER.bindAndGetConnectionString();
    private static final Gson GSON = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SimpleRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DynamicPropertySource
    static void configureMongo(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", () -> MONGO_URI);
        registry.add("spring.mongodb.database", () -> DATABASE);
    }

    @AfterAll
    static void stopMongoServer() {
        MONGO_SERVER.shutdown();
    }

    @BeforeEach
    void clearDocuments() {
        repository.deleteAll();
    }

    @Test
    void createsAndReadsAnObjectIdBackedDocumentFromTheConfiguredDatabase() throws Exception {
        String request = GSON.toJson(new SimpleDto(null, "apple"));

        String response = mockMvc.perform(post("/api/simple")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        SimpleDto created = GSON.fromJson(response, SimpleDto.class);
        assertThat(created.id()).isNotBlank();
        assertThat(ObjectId.isValid(created.id())).isTrue();
        assertThat(created.name()).isEqualTo("apple");

        mockMvc.perform(get("/api/simple/{id}", created.id()))
                .andExpect(status().isOk());

        assertThat(mongoTemplate.getDb().getName()).isEqualTo(DATABASE);
        assertThat(mongoTemplate.getCollection("simple").countDocuments()).isEqualTo(1);
        Document storedDocument = mongoTemplate.getCollection("simple").find().first();
        assertThat(storedDocument).isNotNull();
        assertThat(storedDocument.get("_id")).isInstanceOf(ObjectId.class);
        assertThat(repository.findById(created.id()).orElseThrow().getName()).isEqualTo("apple");
    }

    @Test
    void updatesAndDeletesTheDocumentByItsMongoId() throws Exception {
        SimpleEntity saved = repository.save(new SimpleEntity(null, "pear"));
        SimpleDto created = new SimpleDto(saved.getId(), saved.getName());
        String update = GSON.toJson(new SimpleDto("different-id", "peach"));

        String response = mockMvc.perform(put("/api/simple/{id}", created.id())
                        .contentType("application/json")
                        .content(update))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(GSON.fromJson(response, SimpleDto.class).name()).isEqualTo("peach");
        assertThat(repository.findById(created.id()).orElseThrow().getName()).isEqualTo("peach");

        mockMvc.perform(delete("/api/simple/{id}", created.id()))
                .andExpect(status().isNoContent());
        assertThat(repository.findById(created.id())).isEmpty();
    }
}
