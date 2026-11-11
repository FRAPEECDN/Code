package com.fp.boot_demo_web.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.SimpleEntity;

/**
 * JDBC slice tests for inserting, querying, updating, and deleting records.
 */
@JdbcTest
@Import(SimpleRepository.class)
@ActiveProfiles("test")
class TestSimpleRepository {

    @Autowired
    private SimpleRepository repository;

    @Test
    void insertsAndReadsRecord() {
        SimpleEntity saved = repository.save(new SimpleEntity(0, "test"));
        Optional<SimpleEntity> result = repository.findById(saved.getId());

        assertTrue(saved.getId() > 0);
        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
        assertEquals("test", result.get().getName());
    }

    @Test
    void listsAndUpdatesRecords() {
        SimpleEntity first = repository.save(new SimpleEntity(0, "first"));
        SimpleEntity second = repository.save(new SimpleEntity(0, "second"));

        repository.save(new SimpleEntity(first.getId(), "changed"));
        var records = repository.findAll();

        assertEquals(2, records.size());
        assertEquals("changed", records.get(0).getName());
        assertEquals(second.getId(), records.get(1).getId());
    }

    @Test
    void deletesRecord() {
        SimpleEntity saved = repository.save(new SimpleEntity(0, "test"));

        repository.delete(saved);

        assertFalse(repository.findById(saved.getId()).isPresent());
    }

}
