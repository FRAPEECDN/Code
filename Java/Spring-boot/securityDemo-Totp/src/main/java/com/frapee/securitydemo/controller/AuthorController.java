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

/**
 * Exposes endpoints that require the {@code AUTHOR} authority.
 */
@RestController
@RequestMapping(path = "/author")
public class AuthorController {

    /** Response body returned by a successful GET request. */
    public static final String MESSAGE = "Author access works!";
    /** Response body returned by a successful POST request. */
    public static final String POSTED = "Author post works!";
    /** Response body returned by a successful PUT request. */
    public static final String PUT = "Author put works!";

    /**
     * Validates that the caller has the author-level authority.
     *
     * @return the author access message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }
        
    /**
     * Creates a resource that requires author-level permissions.
     *
     * @param entity request payload
     * @return a confirmation message for the POST request
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@RequestBody String entity) {
        return POSTED;
    }

    /**
     * Updates an author-scoped resource.
     *
     * @param id resource identifier
     * @param entity updated payload
     * @return a confirmation message for the PUT request
     */
    @PutMapping(value="/{id}")
    public String put(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Deletes an author-scoped resource.
     *
     * @param id resource identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    } 

}
