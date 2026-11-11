package com.fp.boot_demo_web.integrated.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import com.fp.boot_demo_web.integrated.TestcontainersConfiguration;
import com.fp.boot_demo_web.model.dto.SimpleDto;

/** Exercises HTTP endpoints through a real server and PostgreSQL container. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class SimpleControllerRestTemplateIT {

    private static final String PATH = "/api/simple";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());

    @LocalServerPort
    private int port;

    /** Seeds the database with records used by the HTTP tests. */
    @BeforeEach
    void setupBaseData() {
        jdbcTemplate.update("DELETE FROM simple");
        jdbcTemplate.update("INSERT INTO simple (name) VALUES (?)", "apple");
        jdbcTemplate.update("INSERT INTO simple (name) VALUES (?)", "banana");
    }

    @Test
    void getsAllRecords() {
        ResponseEntity<SimpleDto[]> response = restTemplate.getForEntity(url(), SimpleDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
    }

    @Test
    void getsRecordById() {
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM simple WHERE name = ?", Integer.class, "banana");

        ResponseEntity<SimpleDto> response = restTemplate.getForEntity(url() + "/" + id, SimpleDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("banana", response.getBody().name());
    }

    @Test
    void createsRecordAndReadsItBack() {
        ResponseEntity<SimpleDto> created = restTemplate.postForEntity(url(),
                new SimpleDto(0, "coconut"), SimpleDto.class);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        SimpleDto saved = created.getBody();

        ResponseEntity<SimpleDto> readBack = restTemplate.getForEntity(url() + "/" + saved.id(), SimpleDto.class);
        assertEquals(HttpStatus.OK, readBack.getStatusCode());
        assertEquals(saved, readBack.getBody());
    }

    @Test
    void updatesRecord() {
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM simple WHERE name = ?", Integer.class, "banana");
        restTemplate.put(url() + "/" + id, new SimpleDto(id, "coconut"));

        ResponseEntity<SimpleDto> updated = restTemplate.getForEntity(url() + "/" + id, SimpleDto.class);
        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertNotNull(updated.getBody());
        assertEquals("coconut", updated.getBody().name());
    }

    @Test
    void deletesRecord() {
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM simple WHERE name = ?", Integer.class, "banana");
        restTemplate.delete(url() + "/" + id);
        assertEquals(0, jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM simple WHERE id = ?", Integer.class, id));
    }

    @Test
    void patchesRecord() {
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM simple WHERE name = ?", Integer.class, "banana");
        restTemplate.patchForObject(url() + "/" + id, new SimpleDto(id, "coconut"), SimpleDto.class);

        assertEquals("coconut", jdbcTemplate.queryForObject(
                "SELECT name FROM simple WHERE id = ?", String.class, id));
    }

    @Test
    void searchesRecordsWithPaging() {
        String uri = UriComponentsBuilder.fromUriString(url() + "/search")
                .queryParam("page", 0)
                .queryParam("size", 1)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        com.google.gson.JsonObject body = com.google.gson.JsonParser.parseString(response.getBody())
                .getAsJsonObject();
        assertEquals(1, body.getAsJsonArray("content").size());
        assertEquals(2, body.get("totalElements").getAsInt());
    }

    @Test
    void searchesRecordsWithDefaultPaging() {
        ResponseEntity<String> response = restTemplate.getForEntity(url() + "/search", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        com.google.gson.JsonObject body = com.google.gson.JsonParser.parseString(response.getBody())
                .getAsJsonObject();
        assertEquals(2, body.getAsJsonArray("content").size());
        assertEquals(2, body.get("totalElements").getAsInt());
    }

    @Test
    void missingRecordReturnsNotFound() {
        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.getForEntity(url() + "/{id}", SimpleDto.class, Integer.MAX_VALUE));
    }

    @Test
    void missingRecordCannotBeUpdated() {
        int missingId = Integer.MAX_VALUE;
        SimpleDto value = new SimpleDto(missingId, "coconut");

        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.put(url() + "/" + missingId, value));
    }

    @Test
    void missingRecordCannotBePatched() {
        int missingId = Integer.MAX_VALUE;
        SimpleDto value = new SimpleDto(missingId, "coconut");

        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.patchForObject(url() + "/" + missingId, value, SimpleDto.class));
    }

    @Test
    void missingRecordCannotBeDeleted() {
        int missingId = Integer.MAX_VALUE;

        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.delete(url() + "/" + missingId));
    }

    private String url() {
        return "http://localhost:" + port + PATH;
    }
}
