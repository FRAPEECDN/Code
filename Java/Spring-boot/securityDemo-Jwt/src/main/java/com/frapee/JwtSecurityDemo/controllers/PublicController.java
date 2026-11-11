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
 * Exposes public endpoints that do not require authentication.
 */
@RestController
@RequestMapping(path = "/public")
public class PublicController {

    /**
     * Creates the public endpoint controller.
     */
    public PublicController() {
    }

    /** Response body returned by {@code GET /public}. */
    public static final String MESSAGE = "public access works!";

    /** Response body returned by {@code POST /public}. */
    public static final String POSTED = "public post works!";

    /** Response body returned by {@code PUT /public/{id}}. */
    public static final String PUT = "public put works!";

    /**
     * Returns a simple message to confirm public access.
     *
     * @return public message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }

    /**
     * Demonstrates a public POST endpoint.
     *
     * @param entity request payload
     * @return confirmation string
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@RequestBody String entity) {
        return POSTED;
    }

    /**
     * Demonstrates a public PUT endpoint.
     *
     * @param id path variable identifier
     * @param entity request payload
     * @return confirmation string
     */
    @PutMapping(value="/{id}")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Demonstrates a public DELETE endpoint.
     *
     * @param id path variable identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }

}
