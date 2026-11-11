package com.frapee.securitydemo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.frapee.securitydemo.controller.AdminController;
import com.frapee.securitydemo.controller.ApiController;
import com.frapee.securitydemo.controller.AuthorController;
import com.frapee.securitydemo.controller.PublicController;
import com.frapee.securitydemo.security.MfaService;

@SpringBootTest
@AutoConfigureMockMvc
class MfaControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MfaService mfaService;

	@Test
	void publicControllerSupportsCrudCallsAndRequiresCsrfForMutations() throws Exception {
		mockMvc.perform(post("/public").content("abc").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());

		assertCrudCalls(null, "/public", PublicController.MESSAGE, PublicController.POSTED,
				PublicController.PUT);
	}

	@Test
	void userControllerSupportsCrudCallsOnlyAfterMfaAndPreservesRoleRules() throws Exception {
		MockHttpSession userSession = loginWithMfa("user");
		assertCrudCalls(userSession, "/api", ApiController.MESSAGE, ApiController.POSTED, ApiController.PUT);

		mockMvc.perform(get("/admin").session(userSession))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/author").session(userSession))
				.andExpect(status().isForbidden());
	}

	@Test
	void authorControllerSupportsCrudCallsOnlyForAuthorAuthority() throws Exception {
		MockHttpSession authorSession = loginWithMfa("author");
		assertCrudCalls(authorSession, "/author", AuthorController.MESSAGE, AuthorController.POSTED,
				AuthorController.PUT);

		mockMvc.perform(get("/admin").session(authorSession))
				.andExpect(status().isForbidden());
	}

	@Test
	void adminControllerSupportsCrudCallsOnlyForAdminRole() throws Exception {
		MockHttpSession adminSession = loginWithMfa("admin");
		assertCrudCalls(adminSession, "/admin", AdminController.MESSAGE, AdminController.POSTED,
				AdminController.PUT);

		mockMvc.perform(get("/api").session(adminSession))
				.andExpect(status().isForbidden());
	}

	private void assertCrudCalls(MockHttpSession session, String path, String getResponse, String postResponse,
			String putResponse) throws Exception {
		perform(get(path).accept(MediaType.APPLICATION_JSON), session)
				.andExpect(status().isOk())
				.andExpect(content().string(getResponse));

		perform(post(path).content("abc").contentType(MediaType.APPLICATION_JSON)
						.with(csrf()).accept(MediaType.APPLICATION_JSON), session)
				.andExpect(status().isCreated())
				.andExpect(content().string(postResponse));

		perform(put(path + "/1").content("abc").contentType(MediaType.APPLICATION_JSON)
						.with(csrf()).accept(MediaType.APPLICATION_JSON), session)
				.andExpect(status().isOk())
				.andExpect(content().string(putResponse));

		perform(delete(path + "/1").with(csrf()).accept(MediaType.APPLICATION_JSON), session)
				.andExpect(status().isNoContent())
				.andExpect(content().string(""));
	}

	private ResultActions perform(MockHttpServletRequestBuilder request, MockHttpSession session) throws Exception {
		return mockMvc.perform(session == null ? request : request.session(session));
	}

	private MockHttpSession loginWithMfa(String username) throws Exception {
		MvcResult loginResult = mockMvc.perform(post("/login").with(csrf())
						.param("username", username)
						.param("password", "password"))
				.andExpect(status().is3xxRedirection())
				.andReturn();
		MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
		assertNotNull(session);

		String secret = mfaService.getSecretForUser(username);
		String code = mfaService.generateCode(secret, Instant.now());
		mockMvc.perform(post("/mfa/verify").session(session).with(csrf()).param("code", code))
				.andExpect(status().is3xxRedirection());
		return session;
	}
}
