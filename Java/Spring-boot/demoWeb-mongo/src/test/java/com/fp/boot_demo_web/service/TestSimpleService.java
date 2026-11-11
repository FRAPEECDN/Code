package com.fp.boot_demo_web.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.exceptions.EntityNotFoundException;
import com.fp.boot_demo_web.exceptions.GeneralServiceException;
import com.fp.boot_demo_web.model.dto.SimpleDto;
import com.fp.boot_demo_web.model.entities.SimpleEntity;
import com.fp.boot_demo_web.repository.SimpleRepository;

/**
 * Unit tests for service operations using a mocked repository.
 */
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TestSimpleService {

    @InjectMocks
    private SimpleService testService;

    @Mock
    private SimpleRepository testRepository;

    private List<SimpleEntity> setupData;

    @BeforeEach
    void setUp() {
        setupData = List.of(
                new SimpleEntity("id-1", "apple"),
                new SimpleEntity("id-2", "pear"),
                new SimpleEntity("id-3", "peach"),
                new SimpleEntity("id-4", "grape"),
                new SimpleEntity("id-5", "orange"));
    }

    @Test
    public void testGetAll() {
        when(testRepository.findAll()).thenReturn(setupData);

        List<SimpleDto> results = testService.getAll();
        assertThat(results.size(), equalTo(setupData.size()));
        for (SimpleDto entry : results) {
            assertThat(entry.name(), not(blankOrNullString()));
        }
    }

    @Test
    public void testGetOne() {
        when(testRepository.findById("id-2")).thenReturn(Optional.of(setupData.get(1)));

        SimpleDto result = testService.getOne("id-2");
        assertThat(result, notNullValue());
        assertThat(result.name(), not(blankOrNullString()));
    }

    @Test
    public void testCreate() {
        when(testRepository.save(isA(SimpleEntity.class))).thenReturn(new SimpleEntity("generated-id", "newValue"));
        SimpleDto value = new SimpleDto("client-provided", "newValue");
        SimpleDto newValue = testService.createOne(value);
        assertThat(newValue.name(), not(blankOrNullString()));
        assertThat(newValue.name(), equalTo(value.name()));
        assertThat(newValue.id(), equalTo("generated-id"));
        ArgumentCaptor<SimpleEntity> savedEntity = ArgumentCaptor.forClass(SimpleEntity.class);
        verify(testRepository).save(savedEntity.capture());
        assertThat(savedEntity.getValue().getId(), nullValue());
    }

    @Test
    public void testUpdate() {
        when(testRepository.findById("id-2")).thenReturn(Optional.of(setupData.get(1)));
        when(testRepository.save(isA(SimpleEntity.class))).thenReturn(new SimpleEntity("id-2", "changedValue"));

        SimpleDto oldValue = testService.getOne("id-2");
        SimpleDto newValue = new SimpleDto(oldValue.id(), "changedValue");
        SimpleDto updateResult = testService.updateOne("id-2", newValue);
        assertThat(updateResult.name(), not(blankOrNullString()));
        assertThat(updateResult.name(), equalTo(newValue.name()));
    }

    @Test
    public void testDelete() {
        when(testRepository.findById("id-2")).thenReturn(Optional.of(setupData.get(1)));
        Mockito.doNothing().when(testRepository).delete(isA(SimpleEntity.class));
        testService.deleteOne("id-2");
    }

    @Test
    public void testCreateWithNullFail() {
        SimpleDto value = null;
        Exception exception = assertThrows(NullPointerException.class, () -> {
            testService.createOne(value);
        });
        assertThat(exception.getMessage(), blankOrNullString());
    }

    @Test
    public void testGetOneFail() {
        when(testRepository.findById(isA(String.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            testService.getOne("missing");
        });
        assertThat(exception.getMessage(), not(blankOrNullString()));
    }

    @Test
    public void testCreateFail() {
        when(testRepository.save(isA(SimpleEntity.class))).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(GeneralServiceException.class, () -> {
            testService.createOne(new SimpleDto(null, "Coconut"));
        });
        assertThat(exception.getMessage(), not(blankOrNullString()));
    }

    @Test
    public void testUpdateFail() {
        when(testRepository.findById(isA(String.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            testService.updateOne("missing", new SimpleDto("missing", "Coconut"));
        });
        assertThat(exception.getMessage(), not(blankOrNullString()));
    }

    @Test
    public void testUpdateSaveFail() {
        when(testRepository.findById(isA(String.class))).thenReturn(Optional.of(setupData.get(1)));
        when(testRepository.save(isA(SimpleEntity.class))).thenThrow(IllegalArgumentException.class);
        Exception exception = assertThrows(GeneralServiceException.class, () -> {
            testService.updateOne("id-2", new SimpleDto("id-2", "Coconut"));
        });
        assertThat(exception.getMessage(), not(blankOrNullString()));
    }

    @Test
    public void testDeleteFail() {
        when(testRepository.findById(isA(String.class))).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            testService.deleteOne("missing");
        });
        assertThat(exception.getMessage(), not(blankOrNullString()));
    }

}
