package com.frapee.securitydemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

/**
 * Contains endpoints intended for authenticated users with the {@code ROLE_USER}
 * privilege.
 */
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class ApiController {

    public static final String MESSAGE = "api access works!";
    public static final String POSTED = "api post works!";
    public static final String PUT = "api put works!";
    public static final String DELETE = "api delete works!";

    /**
     * Returns a confirmation that the caller is authorized at the user level.
     *
     * @return the user access message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }

    /**
     * Creates a user-scoped resource.
     *
     * @param entity request payload
     * @return a confirmation that the POST request succeeded
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@RequestBody String entity) {
        return POSTED;
    }

    /**
     * Updates a user-scoped resource.
     *
     * @param id resource identifier
     * @param entity replacement payload
     * @return a confirmation that the PUT request succeeded
     */
    @PutMapping(value="/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Deletes a user-scoped resource.
     *
     * @param id resource identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }

}
