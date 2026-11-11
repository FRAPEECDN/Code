package com.frapee.JwtSecurityDemo.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Intercepts incoming requests and loads the authenticated user from a JWT bearer token.
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final JwtService service;

    private final UserDetailsService userDetailsService;

    /**
     * Creates the filter with the JWT service and user details lookup dependency.
     *
     * @param service helper for validating and reading JWT claims
     * @param userDetailsService lookup service used to resolve the authenticated principal
     */
    public JwtAuthenticationFilter(JwtService service, UserDetailsService userDetailsService) {
        this.service = service;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Validates a bearer token and stores the authenticated principal in the security context.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param filterChain remaining filter chain
     * @throws ServletException if the filter is unable to continue the chain
     * @throws IOException if the filter cannot handle the request stream
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String jwt = parseJwt(request);
            if (jwt != null && service.validateJwtToken(jwt)) {
                String username = service.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT value from the Authorization header.
     *
     * @param request current request
     * @return JWT value without the Bearer prefix, or null if absent
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
