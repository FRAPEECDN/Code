package com.frapee.securitydemo.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Replaces the password-only authentication with a pending-MFA authentication.
 * The user's actual authorities are deliberately withheld until the TOTP
 * challenge succeeds.
 */
@Component
public class MfaAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final SecurityContextRepository securityContextRepository;

	/**
	 * Creates a success handler that persists the MFA-pending security context.
	 *
	 * @param securityContextRepository HTTP session context repository
	 */
	public MfaAuthenticationSuccessHandler(SecurityContextRepository securityContextRepository) {
		this.securityContextRepository = securityContextRepository;
	}

	/**
	 * Stores an authentication with no application roles and sends the browser
	 * to the TOTP challenge.
	 *
	 * @param request current login request
	 * @param response response to redirect
	 * @param authentication successful password authentication
	 * @throws IOException if the redirect or session write fails
	 * @throws ServletException if the request cannot be completed
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		UsernamePasswordAuthenticationToken pendingAuthentication = new UsernamePasswordAuthenticationToken(
				authentication.getPrincipal(), null,
				List.of(new SimpleGrantedAuthority("MFA_PENDING")));
		pendingAuthentication.setDetails(authentication.getDetails());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(pendingAuthentication);
		SecurityContextHolder.setContext(context);
		securityContextRepository.saveContext(context, request, response);
		response.sendRedirect(request.getContextPath() + "/mfa");
	}
}
