package com.fp.boot_demo_web.model.validation.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.dto.SimpleDto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link SimpleDto} input constraints. */
@ActiveProfiles("test")
public class TestSimpleDtoValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies DTOs that satisfy the record name constraints. */
    static Stream<SimpleDto> validSimpleDtos() {
        return Stream.of(
                new SimpleDto(null, "abc"),
                new SimpleDto("id-1", "normal range"),
                new SimpleDto("id-2", "abcdefhijklmnopqrstuvwxyzabcdefhijklmnopqrstuvwx"));
    }

    /** Supplies DTOs and expected property paths for invalid values. */
    static Stream<Arguments> inValidSimpleDtos() {
        return Stream.of(
                Arguments.of(new SimpleDto(null, ""), List.of("name", "name")),
                Arguments.of(new SimpleDto("id-1", null), List.of("name")),
                Arguments.of(new SimpleDto("id-2", "abcdefhijklmnopqrstuvwxyzabcdefhijklmnopqrstuvwxyzabcdef"),
                        List.of("name")));
    }

    /** Confirms every supplied valid DTO has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validSimpleDtos")
    public void testValid(SimpleDto valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid DTO values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("inValidSimpleDtos")
    void testInvalid(SimpleDto invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
