package com.frapee.JwtSecurityDemo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.frapee.JwtSecurityDemo.jwt.JwtResponse;
import com.frapee.JwtSecurityDemo.jwt.JwtService;
import com.frapee.JwtSecurityDemo.jwt.LoginRequest;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

/**
 * Authenticates users and issues JWT bearer tokens for the demo application.
 */
@RestController
public class JwtController {

    final AuthenticationManager authenticationManager;

    final JwtService service;

    /**
     * Creates the controller with its authentication and token-generation services.
     *
     * @param authenticationManager verifies submitted credentials
     * @param service creates JWTs for authenticated users
     */
    JwtController(AuthenticationManager authenticationManager, JwtService service) {
        this.authenticationManager = authenticationManager;
        this.service = service;
    }

    /**
     * Authenticates a user and returns a JWT token in the response body.
     *
     * @param loginRequest username and password submitted by the caller
     * @return HTTP 200 response containing the issued token and username
     */
    @PostMapping("/signin")
    @SecurityRequirements()
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.name(), loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDetails = (User) authentication.getPrincipal();
        String jwt = service.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername()));

    }

}
