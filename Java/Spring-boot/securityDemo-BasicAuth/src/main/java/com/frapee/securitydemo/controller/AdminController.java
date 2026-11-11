package com.frapee.securitydemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Exposes administrative endpoints restricted to users with the
 * {@code ROLE_ADMIN} authority. Each endpoint uses
 * {@link RolesAllowed @RolesAllowed} with the unprefixed role name
 * {@code ADMIN}; Spring Security applies its default {@code ROLE_} prefix.
 */
@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
public class AdminController {

    public static final String MESSAGE = "admin access works!";
    public static final String POSTED = "admin post works!";
    public static final String PUT = "admin put works!";

    /**
     * Confirms that the caller has administrative permissions.
     *
     * @return the admin access message
     */
    @GetMapping
    @RolesAllowed("ADMIN")
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
    @RolesAllowed("ADMIN")
    public String post(@RequestBody String entity) {
        return POSTED;
    }
    
    /**
     * Updates an administrative resource.
     *
     * @param id resource identifier
     * @param entity updated payload
     * @return a confirmation message for the PUT operation
     */
    @PutMapping(value="/{id}")
    @RolesAllowed("ADMIN")
    public String putMethodName(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Deletes an administrative resource.
     *
     * @param id resource identifier
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RolesAllowed("ADMIN")
    public void delete(@PathVariable String id) {

    }

}
