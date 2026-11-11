package com.frapee.securitydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * Defines the application's authentication and authorization rules.
 * <p>
 * This configuration registers an in-memory user store, configures endpoint
 * access control and JSR-250 method security, enables HTTP Basic and form login,
 * and applies CSRF protection for cookie-based APIs.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
public class ConfigSecurity {

	/**
	 * Creates the in-memory user store used by the demo application.
	 *
	 * @return a {@link UserDetailsService} with demo credentials and roles
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withDefaultPasswordEncoder().username("bla").password("password").build());
		manager.createUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER") .build());
        manager.createUser(User.withDefaultPasswordEncoder().username("admin").password("password").roles("ADMIN").build());
		manager.createUser(User.withDefaultPasswordEncoder().username("author").password("password").authorities("ROLE_USER", "AUTHOR").build());
		return manager;
	}

	/**
	 * Uses a cookie-backed CSRF token repository so token-based protection works
	 * with browser-driven clients and JavaScript access.
	 *
	 * @return the configured CSRF token repository
	 */
	@Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }	

    /**
	 * Defines which URL patterns require authentication and which roles or
	 * authorities are necessary to access them.
	 *
	 * @param http the Spring Security HTTP configuration object
	 * @return the security filter chain for the application
	 * @throws Exception if the security configuration cannot be built
	 */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
			.csrf((csrf) -> csrf
					.csrfTokenRepository(csrfTokenRepository())
					.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))		
            .authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/api/**")
				.hasRole("USER")
				.requestMatchers("/admin/**")
				.hasRole("ADMIN")
				.requestMatchers("/author/**")
				.hasAnyAuthority("AUTHOR")
				.requestMatchers("/v3/**","/swagger-ui/**")
				.authenticated()
                .requestMatchers("/public/**")
				.permitAll()
			)

			.httpBasic(Customizer.withDefaults())
			.formLogin(Customizer.withDefaults())
            .build();            
    }

}
