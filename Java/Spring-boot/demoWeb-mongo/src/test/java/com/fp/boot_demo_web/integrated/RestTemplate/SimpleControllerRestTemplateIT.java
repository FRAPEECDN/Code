package com.fp.boot_demo_web.integrated.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fp.boot_demo_web.integrated.TestcontainersConfiguration;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.model.entities.SimpleEntity;
import com.fp.boot_demo_web.repository.SimpleRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/** Exercises the running HTTP API and MongoDB persistence with RestTemplate. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
class SimpleControllerRestTemplateIT {

    private static final String PATH = "/api/simple";
    private static final Gson GSON = new Gson();

    @Autowired
    private SimpleRepository repository;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());

    @LocalServerPort
    private int port;

    @BeforeEach
    void seedDocuments() {
        repository.deleteAll();
        repository.saveAll(List.of(
                new SimpleEntity(null, "apple"),
                new SimpleEntity(null, "banana"),
                new SimpleEntity(null, "grape")));
    }

    @Test
    void getsAllRecords() {
        ResponseEntity<SimpleDto[]> response = restTemplate.getForEntity(url(), SimpleDto[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).extracting(SimpleDto::name)
                .containsExactlyInAnyOrder("apple", "banana", "grape");
    }

    @Test
    void getsRecordById() {
        String id = findId("banana");

        ResponseEntity<SimpleDto> response = restTemplate.getForEntity(url() + "/" + id, SimpleDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new SimpleDto(id, "banana"));
    }

    @Test
    void createsRecordAndReadsItBack() {
        ResponseEntity<SimpleDto> created = restTemplate.postForEntity(url(),
                new SimpleDto("client-supplied-id", "coconut"), SimpleDto.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        SimpleDto saved = created.getBody();
        assertThat(ObjectId.isValid(saved.id())).isTrue();

        ResponseEntity<SimpleDto> readBack = restTemplate.getForEntity(url() + "/" + saved.id(), SimpleDto.class);
        assertThat(readBack.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(readBack.getBody()).isEqualTo(saved);
        assertThat(repository.count()).isEqualTo(4);
    }

    @Test
    void updatesRecordByPathId() {
        String id = findId("banana");

        restTemplate.put(url() + "/" + id, new SimpleDto("different-id", "coconut"));

        ResponseEntity<SimpleDto> updated = restTemplate.getForEntity(url() + "/" + id, SimpleDto.class);
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody()).isEqualTo(new SimpleDto(id, "coconut"));
        assertThat(repository.count()).isEqualTo(3);
    }

    @Test
    void patchesRecordByPathId() {
        String id = findId("banana");

        SimpleDto patched = restTemplate.patchForObject(url() + "/" + id,
                new SimpleDto("different-id", "coconut"), SimpleDto.class);

        assertThat(patched).isEqualTo(new SimpleDto(id, "coconut"));
        assertThat(repository.findById(id)).get()
                .extracting(SimpleEntity::getName)
                .isEqualTo("coconut");
    }

    @Test
    void deletesRecord() {
        String id = findId("banana");

        restTemplate.delete(url() + "/" + id);

        assertThat(repository.findById(id)).isEmpty();
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void searchesRecordsWithPaging() {
        String uri = UriComponentsBuilder.fromUriString(url() + "/search")
                .queryParam("page", 0)
                .queryParam("size", 2)
                .toUriString();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonObject body = JsonParser.parseString(response.getBody()).getAsJsonObject();
        assertThat(body.getAsJsonArray("content")).hasSize(2);
        assertThat(body.get("totalElements").getAsInt()).isEqualTo(3);
    }

    @Test
    void searchesRecordsWithDefaultPaging() {
        ResponseEntity<String> response = restTemplate.getForEntity(url() + "/search", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonObject body = JsonParser.parseString(response.getBody()).getAsJsonObject();
        assertThat(body.getAsJsonArray("content")).hasSize(3);
        assertThat(body.get("totalElements").getAsInt()).isEqualTo(3);
    }

    @Test
    void missingRecordReturnsNotFound() {
        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.getForEntity(url() + "/{id}", SimpleDto.class, new ObjectId().toHexString()));
    }

    @Test
    void missingRecordCannotBeUpdated() {
        String id = new ObjectId().toHexString();

        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.put(url() + "/" + id, new SimpleDto(id, "coconut")));
    }

    @Test
    void missingRecordCannotBePatched() {
        String id = new ObjectId().toHexString();

        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.patchForObject(url() + "/" + id, new SimpleDto(id, "coconut"), SimpleDto.class));
    }

    @Test
    void missingRecordCannotBeDeleted() {
        String id = new ObjectId().toHexString();

        assertThrows(HttpClientErrorException.NotFound.class,
                () -> restTemplate.delete(url() + "/" + id));
    }

    @Test
    void rejectsInvalidRecordName() {
        assertThrows(HttpClientErrorException.BadRequest.class,
                () -> restTemplate.postForEntity(url(), new SimpleDto(null, "ab"), SimpleDto.class));
    }

    private String findId(String name) {
        return repository.findAll().stream()
                .filter(entity -> entity.getName().equals(name))
                .map(SimpleEntity::getId)
                .findFirst()
                .orElseThrow();
    }

    private String url() {
        return "http://localhost:" + port + PATH;
    }
}
