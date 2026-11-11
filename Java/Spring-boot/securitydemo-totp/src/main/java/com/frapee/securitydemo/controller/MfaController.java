package com.frapee.securitydemo.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.frapee.securitydemo.security.MfaService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Serves the second-factor challenge and promotes a pending session only after
 * a valid TOTP code is submitted.
 */
@RestController
public class MfaController {

	private final MfaService mfaService;
	private final UserDetailsService userDetailsService;
	private final SecurityContextRepository securityContextRepository;

	/**
	 * Creates the MFA endpoints.
	 *
	 * @param mfaService                TOTP secret and verification service
	 * @param userDetailsService        source of the user's final authorities
	 * @param securityContextRepository session authentication persistence
	 */
	public MfaController(MfaService mfaService, UserDetailsService userDetailsService,
			SecurityContextRepository securityContextRepository) {
		this.mfaService = mfaService;
		this.userDetailsService = userDetailsService;
		this.securityContextRepository = securityContextRepository;
	}

	/**
	 * Displays a challenge page and the demo user's authenticator setup URI.
	 *
	 * @param authentication password-authenticated user in the pending-MFA state
	 * @param csrfToken      token required to submit the challenge
	 * @param error          whether the previous TOTP attempt was rejected
	 * @return an HTML challenge page
	 */
	@GetMapping(value = "/mfa", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> challengePage(Authentication authentication,
			@RequestAttribute("_csrf") CsrfToken csrfToken,
			@RequestParam(defaultValue = "false") boolean error) {
		String username = authentication.getName();
		String secret = mfaService.getSecretForUser(username);
		String provisioningUri = "otpauth://totp/"
				+ encodePath("SecurityDemo:" + username)
				+ "?secret=" + secret
				+ "&issuer=SecurityDemo"
				+ "&algorithm=SHA1&digits=6&period=30";

		String html = """
				<!doctype html>
				<html lang="en">
				<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
				<title>Security Demo - MFA</title></head>
				<body>
				<main>
				<h1>Two-factor verification</h1>
				<p>Password accepted for <strong>%s</strong>. Enter the current six-digit code from your authenticator app.</p>
				%s
				<form method="post" action="/mfa/verify">
				<input type="hidden" name="%s" value="%s">
				<label for="code">Authenticator code</label>
				<input id="code" name="code" type="text" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" required autofocus>
				<button type="submit">Verify</button>
				</form>
				<section>
				<h2>Demo authenticator setup</h2>
				<p>Add this key manually to an authenticator app using time-based (TOTP) codes:</p>
				<p><code>%s</code></p>
				<p>Provisioning URI: <code>%s</code></p>
				<p>This randomly generated demo key lasts only until the application restarts.</p>
				</section>
				</main>
				</body></html>
				"""
				.formatted(escapeHtml(username),
						error ? "<p role=\"alert\">That code was not accepted. Try the current code from your app.</p>"
								: "",
						escapeHtml(csrfToken.getParameterName()), escapeHtml(csrfToken.getToken()),
						escapeHtml(secret), escapeHtml(provisioningUri));
		return ResponseEntity.ok().cacheControl(CacheControl.noStore())
				.contentType(MediaType.TEXT_HTML).body(html);
	}

	/**
	 * Verifies a TOTP code, then restores the user's application authorities in
	 * the session. A failed verification leaves the session pending.
	 *
	 * @param code           submitted six-digit code
	 * @param authentication pending password authentication
	 * @param request        current request
	 * @param response       current response
	 * @return a redirect to the dashboard after success or back to the challenge
	 *         after an invalid code
	 */
	@PostMapping("/mfa/verify")
	public ResponseEntity<Void> verifyCode(@RequestParam String code, Authentication authentication,
			HttpServletRequest request, HttpServletResponse response) {
		String username = authentication.getName();
		String secret = mfaService.getSecretForUser(username);
		if (!mfaService.verifyCode(secret, code, Instant.now())) {
			return ResponseEntity.status(HttpStatus.FOUND)
					.header(HttpHeaders.LOCATION, request.getContextPath() + "/mfa?error=true").build();
		}

		UserDetails user = userDetailsService.loadUserByUsername(username);
		List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
		authorities.add(new SimpleGrantedAuthority("MFA_COMPLETE"));
		UsernamePasswordAuthenticationToken completedAuthentication = new UsernamePasswordAuthenticationToken(
				user, null, authorities);
		completedAuthentication.setDetails(authentication.getDetails());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(completedAuthentication);
		SecurityContextHolder.setContext(context);
		securityContextRepository.saveContext(context, request, response);
		return ResponseEntity.status(HttpStatus.FOUND)
				.header(HttpHeaders.LOCATION, request.getContextPath() + "/dashboard").build();
	}

	/**
	 * Shows available demo endpoints after both authentication factors succeed.
	 *
	 * @param authentication fully authenticated user
	 * @param csrfToken      token used by the logout form
	 * @return an HTML dashboard
	 */
	@GetMapping(value = "/dashboard", produces = MediaType.TEXT_HTML_VALUE)
	public ResponseEntity<String> dashboard(Authentication authentication,
			@RequestAttribute("_csrf") CsrfToken csrfToken) {
		String html = """
				<!doctype html>
				<html lang="en">
				<head><meta charset="utf-8"><meta name="viewport" content="width=device-width, initial-scale=1">
				<title>Security Demo - Authenticated</title></head>
				<body>
				<main>
				<h1>Authentication complete</h1>
				<p>Both the password and TOTP factors were verified for <strong>%s</strong>.</p>
				<ul>
				<li><a href="/public">Public endpoint</a></li>
				<li><a href="/api">User endpoint</a></li>
				<li><a href="/author">Author endpoint</a></li>
				<li><a href="/admin">Admin endpoint</a></li>
				<li><a href="/swagger-ui/index.html">API documentation</a></li>
				</ul>
				<form method="post" action="/logout">
				<input type="hidden" name="%s" value="%s">
				<button type="submit">Sign out</button>
				</form>
				</main>
				</body></html>
				""".formatted(escapeHtml(authentication.getName()), escapeHtml(csrfToken.getParameterName()),
				escapeHtml(csrfToken.getToken()));
		return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
	}

	private static String encodePath(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
	}

	private static String escapeHtml(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\"", "&quot;").replace("'", "&#39;");
	}
}
