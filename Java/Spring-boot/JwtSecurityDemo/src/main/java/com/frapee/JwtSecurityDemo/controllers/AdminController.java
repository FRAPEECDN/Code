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
 * Protected admin routes requiring the administrator role.
 */
@RestController
@RequestMapping(path = "/admin")
public class AdminController {

    /**
     * Creates the administrator-protected controller.
     */
    public AdminController() {
    }

    /** Response body returned by {@code GET /admin}. */
    public static final String MESSAGE = "Admin access works!";

    /** Response body returned by {@code POST /admin}. */
    public static final String POSTED = "Admin post works!";

    /** Response body returned by {@code PUT /admin/{id}}. */
    public static final String PUT = "Admin put works!";

    /**
     * Returns a success message when the caller is authorized as admin.
     *
     * @return admin-only access message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }

    /**
     * Verifies POST support for admin-only routes.
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
     * Verifies PUT support for admin-only routes.
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
     * Demonstrates DELETE support for administrator resources.
     *
     * @param id entity identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }

}
