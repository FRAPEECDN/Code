package com.fp.boot_demo_web.model.validation.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.dto.StudentProfileDto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link StudentProfileDto} input constraints. */
@ActiveProfiles("test")
public class TestStudentProfileDtoValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies DTOs with valid profile values and student IDs. */
    static Stream<StudentProfileDto> validStudentProfileDtos() {
        return Stream.of(
                new StudentProfileDto(null, "alex@example.com", null, 1),
                new StudentProfileDto(2, "jordan.lee@example.com", "+1-555-0100", 3));
    }

    /** Supplies DTOs and expected property paths for invalid values. */
    static Stream<Arguments> invalidStudentProfileDtos() {
        return Stream.of(
                Arguments.of(new StudentProfileDto(null, null, null, 1), List.of("email")),
                Arguments.of(new StudentProfileDto(null, " ", null, 1), List.of("email", "email")),
                Arguments.of(new StudentProfileDto(null, "not-an-email", null, 1), List.of("email")),
                Arguments.of(new StudentProfileDto(null, "x".repeat(140) + "@example.com", null, 1),
                        List.of("email", "email")),
                Arguments.of(new StudentProfileDto(null, "alex@example.com", "x".repeat(31), 1), List.of("phone")),
                Arguments.of(new StudentProfileDto(null, "alex@example.com", null, null), List.of("studentId")),
                Arguments.of(new StudentProfileDto(null, "alex@example.com", null, 0), List.of("studentId")));
    }

    /** Confirms every supplied valid DTO has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validStudentProfileDtos")
    public void testValid(StudentProfileDto valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid DTO values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidStudentProfileDtos")
    void testInvalid(StudentProfileDto invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
