package com.frapee.JwtSecurityDemo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.frapee.JwtSecurityDemo.controllers.ApiController;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestApiController {

    private static final String PATH = "/api";
    private static final String HEADER_KEY = "Content-Type";

    @Autowired
    private MockMvc mvc;    

    @Test
    @WithMockUser(roles={"ADMIN"})
    public void testGetAsAdmin() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andReturn();

        assertEquals(expected, result.getResponse().getContentAsString());
    }
    
    @Test
    @WithMockUser(roles={"USER"})
    public void testGetAsUser() throws Exception {
        String expected = ApiController.MESSAGE;
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(header().exists(HEADER_KEY))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andReturn();

        assertEquals(expected, result.getResponse().getContentAsString());
    }
    
    @Test
    @WithMockUser(roles={"USER"})
    public void testPostAsUser() throws Exception {
        String expected = ApiController.POSTED;
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
    @WithMockUser(roles={"USER"})
    public void testPostAsAdminNoCsrf() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(PATH)
            .content("abc")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());            
    }

    @Test
    @WithMockUser(roles={"ADMIN"})
    public void testPostAsAdmin() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(PATH)
            .content("abc")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf().asHeader())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());            
    }    

    @Test
    @WithMockUser(roles={})
    public void testPostAsNoRole() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(PATH)
            .content("abc")
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf().asHeader())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());            
    }      

    @Test
    @WithMockUser(roles={"USER"})
    public void testPutAsUser() throws Exception {
        String expected = ApiController.PUT;
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
    @WithMockUser(roles={"USER"})
    public void testDeleteAsUser() throws Exception {
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
    @WithMockUser(roles={})
    public void testGetAsNoRole() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andReturn();

        assertEquals(expected, result.getResponse().getContentAsString());
    }

    @Test
    @WithAnonymousUser
    public void testGetAnyUser() throws Exception {
        String expected = "";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andReturn();

        assertEquals(expected, result.getResponse().getContentAsString());
    }

}
