package com.frapee.securitydemo;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.frapee.securitydemo.security.MfaService;

@SpringBootTest
@AutoConfigureMockMvc
class MfaLoginFlowTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MfaService mfaService;

	@Test
	void withholdsRolesUntilTotpIsVerified() throws Exception {
		MockHttpSession session = loginWithPassword("user");

		mockMvc.perform(get("/api").session(session))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/dashboard").session(session))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/mfa/verify").session(session).with(csrf()).param("code", "not-a-code"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/mfa?error=true"));
		mockMvc.perform(get("/api").session(session))
				.andExpect(status().isForbidden());

		String code = mfaService.generateCode(mfaService.getSecretForUser("user"), Instant.now());
		mockMvc.perform(post("/mfa/verify").session(session).with(csrf()).param("code", code))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/dashboard"));

		mockMvc.perform(get("/dashboard").session(session))
				.andExpect(status().isOk())
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Authentication complete")));
		mockMvc.perform(get("/v3/api-docs").session(session))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api").session(session))
				.andExpect(status().isOk())
				.andExpect(content().string("api access works!"));
		mockMvc.perform(get("/admin").session(session))
				.andExpect(status().isForbidden());
	}

	@Test
	void grantsAuthorAuthorityOnlyAfterTheSecondFactor() throws Exception {
		MockHttpSession session = loginWithPassword("author");
		String code = mfaService.generateCode(mfaService.getSecretForUser("author"), Instant.now());

		mockMvc.perform(post("/mfa/verify").session(session).with(csrf()).param("code", code))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/author").session(session))
				.andExpect(status().isOk())
				.andExpect(content().string("Author access works!"));
		mockMvc.perform(get("/admin").session(session))
				.andExpect(status().isForbidden());
	}

	private MockHttpSession loginWithPassword(String username) throws Exception {
		MvcResult result = mockMvc.perform(post("/login").with(csrf())
						.param("username", username)
						.param("password", "password"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/mfa"))
				.andReturn();
		return (MockHttpSession) result.getRequest().getSession(false);
	}
}
