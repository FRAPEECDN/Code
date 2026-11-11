package com.frapee.securitydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.frapee.securitydemo.security.MfaAuthenticationSuccessHandler;

/**
 * Configures password authentication, the pending-MFA state, CSRF, and
 * authorization for the demonstration endpoints.
 */
@Configuration
@EnableWebSecurity
public class ConfigSecurity {

	/**
	 * Provides a password encoder for the in-memory demonstration accounts.
	 *
	 * @return a BCrypt password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Creates the demonstration accounts and their roles.
	 *
	 * @param passwordEncoder encoder used to store demo passwords
	 * @return the in-memory user store
	 */
	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		manager.createUser(User.withUsername("bla").password(passwordEncoder.encode("password")).build());
		manager.createUser(User.withUsername("user").password(passwordEncoder.encode("password")).roles("USER").build());
		manager.createUser(User.withUsername("admin").password(passwordEncoder.encode("password")).roles("ADMIN").build());
		manager.createUser(User.withUsername("author").password(passwordEncoder.encode("password"))
				.authorities("ROLE_USER", "AUTHOR").build());
		return manager;
	}

	/**
	 * Creates the session repository used to persist authentication between the
	 * password and TOTP steps.
	 *
	 * @return an HTTP-session-backed security context repository
	 */
	@Bean
	public SecurityContextRepository securityContextRepository() {
		return new HttpSessionSecurityContextRepository();
	}

	/**
	 * Creates a cookie-backed CSRF token repository for browser forms and
	 * JavaScript clients.
	 *
	 * @return the configured CSRF token repository
	 */
	@Bean
	public CsrfTokenRepository csrfTokenRepository() {
		return CookieCsrfTokenRepository.withHttpOnlyFalse();
	}

	/**
	 * Requires a password and TOTP before granting access to the user's normal
	 * roles. Password login receives only {@code MFA_PENDING}; successful TOTP
	 * verification replaces it with the user's roles and {@code MFA_COMPLETE}.
	 *
	 * @param http Spring Security HTTP configuration
	 * @param csrfTokenRepository CSRF token storage
	 * @param securityContextRepository session security-context storage
	 * @param successHandler handler that starts the pending-MFA state
	 * @return the application's security filter chain
	 * @throws Exception if the filter chain cannot be configured
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http,
			CsrfTokenRepository csrfTokenRepository,
			SecurityContextRepository securityContextRepository,
			MfaAuthenticationSuccessHandler successHandler) throws Exception {
		return http
				.securityContext(context -> context.securityContextRepository(securityContextRepository))
				.csrf(csrf -> csrf
						.csrfTokenRepository(csrfTokenRepository)
						.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/login", "/error").permitAll()
						.requestMatchers("/mfa", "/mfa/verify").hasAuthority("MFA_PENDING")
						.requestMatchers("/dashboard").hasAuthority("MFA_COMPLETE")
						.requestMatchers("/public/**").permitAll()
						.requestMatchers("/api/**").hasRole("USER")
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/author/**").hasAuthority("AUTHOR")
						.requestMatchers("/v3/**", "/swagger-ui/**").hasAuthority("MFA_COMPLETE")
						.anyRequest().hasAuthority("MFA_COMPLETE"))
				.formLogin(form -> form.successHandler(successHandler).permitAll())
				.logout(logout -> logout.permitAll())
				.build();
	}
}
