package com.frapee.JwtSecurityDemo.jwt;

import lombok.Getter;

/**
 * Response payload returned after a user successfully signs in.
 */
@Getter
public class JwtResponse {
    private String token;
    private String user;
    private String type = "Bearer";

    /**
     * Creates a bearer-token authentication response.
     *
     * @param token JWT value issued to the authenticated user
     * @param user authenticated username
     */
    public JwtResponse(String token, String user) {
        this.token = token;
        this.user = user;
    }

}
