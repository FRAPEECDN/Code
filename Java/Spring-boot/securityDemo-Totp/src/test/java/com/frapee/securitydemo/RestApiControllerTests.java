package com.frapee.securitydemo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import com.frapee.securitydemo.controller.AdminController;
import com.frapee.securitydemo.controller.ApiController;
import com.frapee.securitydemo.controller.AuthorController;
import com.frapee.securitydemo.controller.PublicController;
import com.frapee.securitydemo.security.MfaService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiControllerTests {

	private static final Pattern CSRF_INPUT = Pattern.compile(
			"<input[^>]*name=\"([^\"]+)\"[^>]*value=\"([^\"]+)\"");
	private static final Pattern TOTP_SECRET = Pattern.compile(
			"<p><code>([A-Z2-7]{32})</code></p>");

	@LocalServerPort
	private int port;

	@Autowired
	private MfaService mfaService;

	@Test
	void publicRestEndpointsSupportCrudCallsAndRequireCsrfForMutations() throws Exception {
		TestHttpClient client = newClient();
		String csrfToken = client.loadLoginCsrfToken();

		assertResponse(client.request("GET", "/public", null, null), HttpStatus.OK, PublicController.MESSAGE);
		assertEquals(HttpStatus.FORBIDDEN.value(),
				client.request("POST", "/public", "abc", null).statusCode());
		assertResponse(client.request("POST", "/public", "abc", csrfToken), HttpStatus.CREATED,
				PublicController.POSTED);
		assertResponse(client.request("PUT", "/public/1", "abc", csrfToken), HttpStatus.OK, PublicController.PUT);
		assertResponse(client.request("DELETE", "/public/1", null, csrfToken), HttpStatus.NO_CONTENT, "");
	}

	@Test
	void userRestEndpointsRequireMfaAndUserRole() throws Exception {
		TestHttpClient user = newClient();
		user.loginWithMfa("user", mfaService);
		assertCrudCalls(user, "/api", ApiController.MESSAGE, ApiController.POSTED, ApiController.PUT);

		assertEquals(HttpStatus.FORBIDDEN.value(), user.request("GET", "/admin", null, null).statusCode());
		assertEquals(HttpStatus.FORBIDDEN.value(), user.request("GET", "/author", null, null).statusCode());
	}

	@Test
	void authorRestEndpointsRequireMfaAndAuthorAuthority() throws Exception {
		TestHttpClient author = newClient();
		author.loginWithMfa("author", mfaService);
		assertCrudCalls(author, "/author", AuthorController.MESSAGE, AuthorController.POSTED, AuthorController.PUT);

		assertEquals(HttpStatus.FORBIDDEN.value(), author.request("GET", "/admin", null, null).statusCode());
	}

	@Test
	void adminRestEndpointsRequireMfaAndAdminRole() throws Exception {
		TestHttpClient admin = newClient();
		admin.loginWithMfa("admin", mfaService);
		assertCrudCalls(admin, "/admin", AdminController.MESSAGE, AdminController.POSTED, AdminController.PUT);

		assertEquals(HttpStatus.FORBIDDEN.value(), admin.request("GET", "/api", null, null).statusCode());
	}

	private void assertCrudCalls(TestHttpClient client, String path, String getResponse, String postResponse,
			String putResponse) throws Exception {
		String csrfToken = client.getMfaCsrfToken();
		assertResponse(client.request("GET", path, null, null), HttpStatus.OK, getResponse);
		assertResponse(client.request("POST", path, "abc", csrfToken), HttpStatus.CREATED, postResponse);
		assertResponse(client.request("PUT", path + "/1", "abc", csrfToken), HttpStatus.OK, putResponse);
		assertResponse(client.request("DELETE", path + "/1", null, csrfToken), HttpStatus.NO_CONTENT, "");
	}

	private TestHttpClient newClient() {
		return new TestHttpClient("http://localhost:" + port);
	}

	private static void assertResponse(HttpResponse<String> response, HttpStatus expectedStatus, String expectedBody) {
		assertEquals(expectedStatus.value(), response.statusCode());
		assertEquals(expectedBody, response.body());
	}

	private static final class TestHttpClient {

		private final String baseUrl;
		private final HttpClient httpClient;
		private String mfaCsrfToken;

		private TestHttpClient(String baseUrl) {
			this.baseUrl = baseUrl;
			CookieManager cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
			httpClient = HttpClient.newBuilder()
					.cookieHandler(cookieManager)
					.followRedirects(HttpClient.Redirect.NEVER)
					.build();
		}

		private String loadLoginCsrfToken() throws Exception {
			HttpResponse<String> response = request("GET", "/login", null, null);
			assertEquals(HttpStatus.OK.value(), response.statusCode());
			return extract(CSRF_INPUT, response.body(), 2);
		}

		private void loginWithMfa(String username, MfaService mfaService) throws Exception {
			String loginCsrfToken = loadLoginCsrfToken();
			String loginBody = form("_csrf", loginCsrfToken)
					+ "&" + form("username", username)
					+ "&" + form("password", "password");
			HttpResponse<String> loginResponse = request("POST", "/login", loginBody, loginCsrfToken);
			assertEquals(HttpStatus.FOUND.value(), loginResponse.statusCode());
			assertTrue(loginResponse.headers().firstValue("location").orElse("").endsWith("/mfa"));

			HttpResponse<String> challenge = request("GET", "/mfa", null, null);
			assertEquals(HttpStatus.OK.value(), challenge.statusCode());
			mfaCsrfToken = extract(CSRF_INPUT, challenge.body(), 2);
			String csrfParameter = extract(CSRF_INPUT, challenge.body(), 1);
			String secret = extract(TOTP_SECRET, challenge.body(), 1);
			String code = mfaService.generateCode(secret, Instant.now());
			String verifyBody = form(csrfParameter, mfaCsrfToken) + "&" + form("code", code);
			HttpResponse<String> verifyResponse = request("POST", "/mfa/verify", verifyBody, mfaCsrfToken);
			assertEquals(HttpStatus.FOUND.value(), verifyResponse.statusCode());
			assertTrue(verifyResponse.headers().firstValue("location").orElse("").endsWith("/dashboard"));
		}

		private String getMfaCsrfToken() {
			assertFalse(mfaCsrfToken == null || mfaCsrfToken.isBlank());
			return mfaCsrfToken;
		}

		private HttpResponse<String> request(String method, String path, String body, String csrfToken)
				throws IOException, InterruptedException {
			HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(baseUrl + path))
					.header("Accept", path.equals("/login") || path.equals("/mfa")
							? "text/html"
							: "application/json");
			if (csrfToken != null) {
				request.header("X-XSRF-TOKEN", csrfToken);
			}
			if (body == null) {
				request.method(method, HttpRequest.BodyPublishers.noBody());
			} else {
				request.header("Content-Type",
						path.equals("/login") || path.equals("/mfa/verify")
								? "application/x-www-form-urlencoded"
								: "application/json");
				request.method(method, HttpRequest.BodyPublishers.ofString(body));
			}
			return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
		}

		private static String extract(Pattern pattern, String body, int group) {
			Matcher matcher = pattern.matcher(body);
			assertTrue(matcher.find(), "Expected pattern was not found in the HTTP response");
			return matcher.group(group);
		}

		private static String form(String name, String value) {
			return URLEncoder.encode(name, StandardCharsets.UTF_8) + "="
					+ URLEncoder.encode(value, StandardCharsets.UTF_8);
		}
	}
}
