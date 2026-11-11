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
 * Exposes endpoints that are intentionally available without requiring any
 * authentication or authorization.
 */
@RestController
@RequestMapping(path = "/public")
public class PublicController {

    /** Response body returned by a successful GET request. */
    public static final String MESSAGE = "public access works!";
    /** Response body returned by a successful POST request. */
    public static final String POSTED = "public post works!";
    /** Response body returned by a successful PUT request. */
    public static final String PUT = "public put works!";

    /**
     * Returns a simple confirmation message proving the route is public.
     *
     * @return the public access response message
     */
    @GetMapping
    public String get() {
        return MESSAGE;
    }
        
    /**
     * Creates a public resource and returns a confirmation message.
     *
     * @param entity request payload passed to the endpoint
     * @return a success message for the POST operation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String post(@RequestBody String entity) {
        return POSTED;
    }

    /**
     * Updates a public resource and returns a confirmation message.
     *
     * @param id identifier of the target resource
     * @param entity updated payload
     * @return a success message for the PUT operation
     */
    @PutMapping(value="/{id}")
    public String put(@PathVariable String id, @RequestBody String entity) {
        return PUT;
    }

    /**
     * Deletes a public resource.
     *
     * @param id identifier of the resource to remove
     */
    @DeleteMapping(value="/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {

    }   

}
