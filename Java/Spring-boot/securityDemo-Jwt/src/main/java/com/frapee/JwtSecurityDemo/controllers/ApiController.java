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
 * Protected API routes requiring the user role.
 */
@RestController
@RequestMapping(path = "/api")
public class ApiController {

    /**
     * Creates the user-protected API controller.
     */
    public ApiController() {
    }

    /** Response body returned by {@code GET /api}. */
    public static final String MESSAGE = "Api access works!";

    /** Response body returned by {@code POST /api}. */
    public static final String POSTED = "Api post works!";

    /** Response body returned by {@code PUT /api/{id}}. */
    public static final String PUT = "Api put works!";

    /**
     * Returns a success message when access is authorized for a user.
     *
     * @return role-protected API message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }

    /**
     * Verifies that POST requests are allowed for authorized users.
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
     * Verifies that PUT requests are allowed for authorized users.
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
     * Demonstrates DELETE support for protected API resources.
     *
     * @param id entity identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }

}
