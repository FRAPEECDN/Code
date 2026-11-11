package com.frapee.securitydemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Exposes administrative endpoints protected by the {@code ROLE_ADMIN}
 * request authorization rule.
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminController {

    /** Response body returned by a successful GET request. */
    public static final String MESSAGE = "admin access works!";
    /** Response body returned by a successful POST request. */
    public static final String POSTED = "admin post works!";
    /** Response body returned by a successful PUT request. */
    public static final String PUT = "admin put works!";

    /**
     * Confirms that the caller has administrative permissions.
     *
     * @return the admin access message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }

    /**
     * Creates a resource that is restricted to administrators.
     *
     * @param entity request payload
     * @return a confirmation message for the POST operation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@RequestBody String entity) {
        return POSTED;
    }

    /**
     * Updates an administrative resource.
     *
     * @param id     resource identifier
     * @param entity updated payload
     * @return a confirmation message for the PUT operation
     */
    @PutMapping(value = "/{id}")
    public String put(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Deletes an administrative resource.
     *
     * @param id resource identifier
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }

}
