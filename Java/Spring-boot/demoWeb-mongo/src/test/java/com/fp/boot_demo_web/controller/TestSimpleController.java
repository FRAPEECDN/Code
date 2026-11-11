package com.fp.boot_demo_web.controller;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fp.boot_demo_web.exceptions.EntityNotFoundException;
import com.fp.boot_demo_web.exceptions.GeneralServiceException;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.service.SimpleService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * MVC slice tests for controller routes, response bodies, and exception
 * handling. The service is mocked so the controller is tested in isolation.
 */
@WebMvcTest(SimpleController.class)
@ActiveProfiles("test")
public class TestSimpleController {

    private static final String PATH = "/api/simple";
    private static final String HEADER_KEY = "Content-Type";

    @MockitoBean
    private SimpleService service;

    @Autowired
    private MockMvc mvc;

    private Gson gson = new Gson();

    @Test
    public void testGetAll() throws Exception {
        final List<SimpleDto> setupData = List.of(
                new SimpleDto("1", "apple"),
                new SimpleDto("2", "grape"),
                new SimpleDto("3", "orange"),
                new SimpleDto("4", "pear"),
                new SimpleDto("5", "peach"),
                new SimpleDto("6", "plum"));
        String expected = gson.toJson(setupData);
        when(service.getAll()).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), equalTo(expected));
        Mockito.verify(this.service, Mockito.times(1)).getAll();
    }

    @Test
    public void testGetOne() throws Exception {
        final SimpleDto setupData = new SimpleDto("1", "apple");
        final String expected = gson.toJson(setupData);
        when(service.getOne(isA(String.class))).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), equalTo(expected));
        Mockito.verify(this.service, Mockito.times(1)).getOne(isA(String.class));
    }

    @Test
    public void testGetOneFail() throws Exception {
        when(service.getOne(isA(String.class))).thenThrow(EntityNotFoundException.class);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof EntityNotFoundException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).getOne(isA(String.class));
    }

    @Test
    public void testSearch() throws Exception {
        final List<SimpleDto> setupData = List.of(
                new SimpleDto("1", "apple"),
                new SimpleDto("2", "banana"),
                new SimpleDto("3", "grape"),
                new SimpleDto("4", "mango"),
                new SimpleDto("5", "nectarine"),
                new SimpleDto("6", "orange"),
                new SimpleDto("7", "pear"),
                new SimpleDto("8", "peach"),
                new SimpleDto("9", "pineapple"),
                new SimpleDto("10", "plum"));
        String expected = gson.toJson(setupData);
        when(service.getAll()).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH + "/search")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();

        String actual = extractJson(result.getResponse().getContentAsString());
        assertThat(actual, equalTo(expected));
        Mockito.verify(this.service, Mockito.times(1)).getAll();
    }

    @Test
    public void testSearchWithPage() throws Exception {
        final List<SimpleDto> setupData = List.of(
                new SimpleDto("1", "apple"),
                new SimpleDto("2", "banana"),
                new SimpleDto("3", "grape"),
                new SimpleDto("4", "mango"),
                new SimpleDto("5", "nectarine"),
                new SimpleDto("6", "orange"),
                new SimpleDto("7", "pear"),
                new SimpleDto("8", "peach"),
                new SimpleDto("9", "pineapple"),
                new SimpleDto("10", "plum"));
        String expected = gson.toJson(setupData.subList(0, 5));
        when(service.getAll()).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(PATH + "/search")
                .param("page", "0")
                .param("size", "5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();

        String actual = extractJson(result.getResponse().getContentAsString());
        assertThat(actual, equalTo(expected));
        Mockito.verify(this.service, Mockito.times(1)).getAll();
    }

    @Test
    public void testCreate() throws Exception {
        final SimpleDto setupData = new SimpleDto("generated-id", "coconut");
        final SimpleDto requestData = new SimpleDto(null, "coconut");
        final String contentData = gson.toJson(requestData);
        when(service.createOne(isA(SimpleDto.class))).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(PATH)
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), equalTo(gson.toJson(setupData)));
        Mockito.verify(this.service, Mockito.times(1)).createOne(isA(SimpleDto.class));
    }

    @Test
    public void testCreateFail() throws Exception {
        SimpleDto setupData = new SimpleDto(null, "coconut");
        String contentData = gson.toJson(setupData);
        when(service.createOne(isA(SimpleDto.class))).thenThrow(GeneralServiceException.class);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(PATH)
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof GeneralServiceException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).createOne(isA(SimpleDto.class));
    }

    @Test
    public void testUpdate() throws Exception {
        SimpleDto setupData = new SimpleDto("1", "coconut");
        String contentData = gson.toJson(setupData);
        when(service.updateOne(isA(String.class), isA(SimpleDto.class))).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", "1")
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), equalTo(contentData));
        Mockito.verify(this.service, Mockito.times(1)).updateOne(isA(String.class), isA(SimpleDto.class));
    }

    @Test
    public void testUpdateFailNoFound() throws Exception {
        SimpleDto setupData = new SimpleDto("1", "coconut");
        String contentData = gson.toJson(setupData);
        when(service.updateOne(isA(String.class), isA(SimpleDto.class))).thenThrow(EntityNotFoundException.class);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", "1")
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof EntityNotFoundException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).updateOne(isA(String.class), isA(SimpleDto.class));
    }

    @Test
    public void testUpdateFailInvalidData() throws Exception {
        SimpleDto setupData = new SimpleDto("1", "coconut");
        String contentData = gson.toJson(setupData);
        when(service.updateOne(isA(String.class), isA(SimpleDto.class))).thenThrow(GeneralServiceException.class);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", "1")
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof GeneralServiceException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).updateOne(isA(String.class), isA(SimpleDto.class));
    }

    @Test
    public void testPatch() throws Exception {
        SimpleDto setupData = new SimpleDto("1", "coconut");
        String contentData = gson.toJson(setupData);
        when(service.updateOne(isA(String.class), isA(SimpleDto.class))).thenReturn(setupData);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.patch(PATH + "/{id}", "1")
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), equalTo(contentData));
        Mockito.verify(this.service, Mockito.times(1)).updateOne(isA(String.class), isA(SimpleDto.class));
    }

    @Test
    public void testPatchFailNoFound() throws Exception {
        SimpleDto setupData = new SimpleDto("1", "coconut");
        String contentData = gson.toJson(setupData);
        when(service.updateOne(isA(String.class), isA(SimpleDto.class))).thenThrow(EntityNotFoundException.class);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.patch(PATH + "/{id}", "1")
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof EntityNotFoundException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).updateOne(isA(String.class), isA(SimpleDto.class));
    }

    @Test
    public void testPatchFailInvalidData() throws Exception {
        SimpleDto setupData = new SimpleDto("1", "coconut");
        String contentData = gson.toJson(setupData);
        when(service.updateOne(isA(String.class), isA(SimpleDto.class))).thenThrow(GeneralServiceException.class);
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.patch(PATH + "/{id}", "1")
                .content(contentData)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof GeneralServiceException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).updateOne(isA(String.class), isA(SimpleDto.class));
    }

    @Test
    public void testDelete() throws Exception {
        Mockito.doNothing().when(service).deleteOne(isA(String.class));
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$").doesNotExist())
                .andReturn();

        assertThat(result.getResponse().getContentType(), nullValue());
        assertThat(result.getResponse().getContentAsByteArray().length, equalTo(0));
        Mockito.verify(this.service, Mockito.times(1)).deleteOne(isA(String.class));
    }

    @Test
    public void testDeleteFail() throws Exception {
        Mockito.doThrow(EntityNotFoundException.class).when(service).deleteOne(isA(String.class));
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().exists(HEADER_KEY))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(expected -> assertTrue(expected.getResolvedException() instanceof EntityNotFoundException))
                .andReturn();

        assertThat(result.getResponse().getContentAsString(), notNullValue());
        Mockito.verify(this.service, Mockito.times(1)).deleteOne(isA(String.class));
    }

    /**
     * Extracts the page content array from a paginated JSON response.
     *
     * @param inputContent serialized paginated response
     * @return serialized value of its {@code content} property
     */
    private String extractJson(String inputContent) {
        try {
            JsonElement content = JsonParser.parseString(inputContent);
            JsonObject jObject = content.getAsJsonObject();
            JsonElement output = jObject.get("content");
            return gson.toJson(output);
        } catch (JsonParseException ex) {
            fail("Not Json");
        }
        return "";
    }
}
