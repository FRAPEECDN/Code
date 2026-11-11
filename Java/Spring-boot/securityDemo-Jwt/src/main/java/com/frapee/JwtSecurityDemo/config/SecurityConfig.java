package com.frapee.JwtSecurityDemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.frapee.JwtSecurityDemo.jwt.JwtAuthenticationFilter;
import com.frapee.JwtSecurityDemo.jwt.JwtService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Central Spring Security configuration for the application.
 *
 * <p>The configuration registers the in-memory user store, JWT authentication
 * filter, stateless session policy, and endpoint authorization rules used by the
 * sample application.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Creates the application's security configuration.
     */
    public SecurityConfig() {
    }

    /**
     * Creates the authentication provider used by Spring Security.
     *
     * @param userDetailsService in-memory user details manager
     * @return a DAO-based authentication provider configured with the default delegating password encoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());

        return authProvider;
    }

    /**
     * Creates the custom JWT filter that extracts bearer tokens from incoming requests.
     *
     * @param jwtService JWT utility service
     * @param userDetailsService user lookup service
     * @return JWT authentication filter
     */
    @Bean
    public JwtAuthenticationFilter authenticationJwtTokenFilter(JwtService jwtService,
            UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    /**
     * Exposes the Spring Security authentication manager.
     *
     * @param authConfig security configuration used to build the manager
     * @return configured authentication manager
     * @throws Exception if the manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Creates a set of demo users used by the in-memory authentication provider.
     *
     * @return user manager with sample credentials and roles
     */
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder().username("bla").password("password").build());
        manager.createUser(
                User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build());
        manager.createUser(
                User.withDefaultPasswordEncoder().username("admin").password("password").roles("ADMIN").build());
        manager.createUser(User.withDefaultPasswordEncoder().username("author").password("password")
                .authorities("AUTHOR").build());
        return manager;
    }

    /**
     * Configures the CSRF token repository used for cookie-based token handling.
     *
     * @return CSRF token repository
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    /**
     * Configures endpoint authorization rules and the JWT filter chain.
     *
     * @param http HTTP security builder
     * @param authenticationProvider active authentication provider
     * @param jwtAuthenticationFilter custom JWT authentication filter
     * @return configured security filter chain
     * @throws Exception if the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/signin")
                        .permitAll()
                        .requestMatchers("/v3/**", "/swagger-ui/**")
                        .permitAll()
                        .requestMatchers("/api/**")
                        .hasRole("USER")
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/author/**")
                        .hasAnyAuthority("AUTHOR")
                        .requestMatchers("/public/**")
                        .permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
