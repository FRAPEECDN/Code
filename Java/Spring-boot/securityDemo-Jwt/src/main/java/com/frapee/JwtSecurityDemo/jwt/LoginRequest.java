package com.frapee.JwtSecurityDemo.jwt;

/**
 * Represents the payload submitted to the sign-in endpoint.
 *
 * @param name user name
 * @param password user password
 */
public record LoginRequest(String name, String password) {

}
