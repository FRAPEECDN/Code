package com.frapee.JwtSecurityDemo.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(AuthorController.class)
public class TestAuthorController {

    private static final String PATH = "/author";
    private static final String HEADER_KEY = "Content-Type";

    @Autowired
    private MockMvc mvc;

    @Test
    @WithMockUser
    public void testGetGood() throws Exception {
        String expected = AuthorController.MESSAGE;
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser
    public void testPostGood() throws Exception {
        String expected = AuthorController.POSTED;
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(PATH)
                .content("abc")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser
    public void testPutGood() throws Exception {
        String expected = AuthorController.PUT;
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", "1")
                .content("abc")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf().asHeader())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser
    public void testDeleteGood() throws Exception {
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", "1")
                .with(csrf().asHeader())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
                .andReturn();
        assertNull(result.getResponse().getContentType());
        assertEquals(result.getResponse().getContentAsByteArray().length, 0);
    }

    @Test
    @WithAnonymousUser
    public void testGetBad() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals(expected, result.getResponse().getContentAsString());
    }

}
