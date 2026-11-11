package com.fp.boot_demo_web.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.fp.boot_demo_web.model.entities.SimpleEntity;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

/** Repository-slice tests for MongoDB document CRUD operations. */
@DataMongoTest
class TestSimpleRepository {

    private static final String DATABASE = "demo_web_mongo_repository_test";
    private static final MongoServer MONGO_SERVER = new MongoServer(new MemoryBackend());
    private static final String MONGO_URI = MONGO_SERVER.bindAndGetConnectionString();

    @Autowired
    private SimpleRepository repository;

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
    void savesAndFindsDocumentWithGeneratedObjectId() {
        SimpleEntity saved = repository.save(new SimpleEntity(null, "test"));

        assertThat(ObjectId.isValid(saved.getId())).isTrue();
        assertThat(repository.findById(saved.getId()))
                .get()
                .usingRecursiveComparison()
                .isEqualTo(saved);
    }

    @Test
    void findsMultipleDocumentsById() {
        SimpleEntity first = repository.save(new SimpleEntity(null, "first"));
        SimpleEntity second = repository.save(new SimpleEntity(null, "second"));

        List<SimpleEntity> found = repository.findAllById(List.of(first.getId(), second.getId()));

        assertThat(found).hasSize(2);
        assertThat(found).extracting(SimpleEntity::getId)
                .containsExactlyInAnyOrder(first.getId(), second.getId());
        assertThat(found).extracting(SimpleEntity::getName)
                .containsExactlyInAnyOrder("first", "second");
    }

    @Test
    void savesUpdatedDocument() {
        SimpleEntity saved = repository.save(new SimpleEntity(null, "before"));
        saved.setName("after");

        repository.save(saved);

        assertThat(repository.findById(saved.getId())).get()
                .extracting(SimpleEntity::getName)
                .isEqualTo("after");
    }

    @Test
    void deletesDocumentById() {
        SimpleEntity saved = repository.save(new SimpleEntity(null, "test"));

        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId())).isEmpty();
        assertThat(repository.count()).isZero();
    }
}
