package com.frapee.securitydemo.integration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import com.frapee.securitydemo.controller.PublicController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestPublicControllerREST {

    private static final String PATH = "/public";
    private static final String USER = "bla";
    private static final String PASSWORD = "password";
    private static final String COOKIE = "Cookie";
    private static final String TOKEN = "XSRF-TOKEN=";

    @Autowired
    private CsrfTokenRepository csrfTokenRepository;

    @LocalServerPort
    int randomServerPort;

    @BeforeEach
    public void activateCsrt() throws Exception{
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = generateSecurityOnlyHeaders();
            
        final HttpEntity<String> httpEntity = new HttpEntity<>("abc", httpHeaders);        
        final ResponseEntity<String> response = restTemplate.exchange(
            generatePath(),
            HttpMethod.POST,
            httpEntity,
            new ParameterizedTypeReference<String>(){}
        );
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.FOUND);
    }

    @Test
    @DirtiesContext
    public void testGet() throws Exception {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = generateHeaders();
        final HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);        
        final ResponseEntity<String> response = restTemplate.exchange(
            generatePath(),
            HttpMethod.GET,
            httpEntity,
            new ParameterizedTypeReference<String>(){}
        );

        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), PublicController.MESSAGE);
    }

    @Test
    @DirtiesContext
    public void testCreate() throws Exception {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = generateHeaders();
        
        final HttpEntity<String> httpEntity = new HttpEntity<>("abc", httpHeaders);        
        final ResponseEntity<String> response = restTemplate.exchange(
            generatePath(),
            HttpMethod.POST,
            httpEntity,
            new ParameterizedTypeReference<String>(){}
        );
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody(), PublicController.POSTED);        
    }

    @Test
    @DirtiesContext
    public void testUpdate() throws Exception {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = generateHeaders();
        final HttpEntity<String> httpEntity = new HttpEntity<>("abc", httpHeaders);        
        final ResponseEntity<String> response = restTemplate.exchange(
            generatePath() + "/1",
            HttpMethod.PUT,
            httpEntity,
            new ParameterizedTypeReference<String>(){}
        );
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), PublicController.PUT);          
    }

    @Test
    @DirtiesContext
    public void testDelete() throws Exception {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders httpHeaders = generateHeaders();
        final HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);        
        final ResponseEntity<Object> response = restTemplate.exchange(
            generatePath() + "/1",
            HttpMethod.DELETE,
            httpEntity,
            new ParameterizedTypeReference<Object>(){}
        );
        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertNull(response.getBody());
    }     

    /**
     * Create the url to be used for testing by providing the correct port and path
     * @return full URL to be used in integrated tests
     */
    private String generatePath() {
        return "http://localhost:" + randomServerPort + PATH;
    }

    private HttpHeaders generateSecurityOnlyHeaders() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(USER, PASSWORD);
        return httpHeaders;        
    }
    
    private HttpHeaders generateHeaders() {
        final CsrfToken csrfToken = csrfTokenRepository.generateToken(null);
        final HttpHeaders httpHeaders = generateSecurityOnlyHeaders();

        httpHeaders.add(csrfToken.getHeaderName(), csrfToken.getToken());
        httpHeaders.add(COOKIE, TOKEN + csrfToken.getToken());

        return httpHeaders;        
    }    
    
}
