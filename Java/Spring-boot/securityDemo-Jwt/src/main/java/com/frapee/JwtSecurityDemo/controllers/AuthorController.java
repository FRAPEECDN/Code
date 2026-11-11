package com.frapee.JwtSecurityDemo.controllers;

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
 * Protected author-only endpoints that require the AUTHOR authority.
 */
@RestController
@RequestMapping(path = "/author")
public class AuthorController {

    /**
     * Creates the author-protected controller.
     */
    public AuthorController() {
    }

    /** Response body returned by {@code GET /author}. */
    public static final String MESSAGE = "Author access works!";

    /** Response body returned by {@code POST /author}. */
    public static final String POSTED = "Author post works!";

    /** Response body returned by {@code PUT /author/{id}}. */
    public static final String PUT = "Author put works!";

    /**
     * Returns a success message when the caller has author privileges.
     *
     * @return author-only access message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }

    /**
     * Verifies POST support for author-authorized users.
     *
     * @param entity request payload
     * @return response message
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@RequestBody String entity) {
        return POSTED;
    }

    /**
     * Verifies PUT support for author-authorized users.
     *
     * @param id entity identifier
     * @param entity request payload
     * @return response message
     */
    @PutMapping(value="/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Demonstrates DELETE support for author-only resources.
     *
     * @param id entity identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }

}
