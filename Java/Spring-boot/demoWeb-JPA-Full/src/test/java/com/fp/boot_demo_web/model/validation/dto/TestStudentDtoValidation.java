package com.fp.boot_demo_web.model.validation.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.dto.StudentDto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link StudentDto} input constraints. */
@ActiveProfiles("test")
public class TestStudentDtoValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies DTOs with valid student values and relationship IDs. */
    static Stream<StudentDto> validStudentDtos() {
        return Stream.of(
                student("Alex Morgan", 1, true, null, Set.of()),
                student("Jordan Lee", 5, false, 3, Set.of(1, 2)));
    }

    /** Supplies DTOs and expected property paths for invalid values. */
    static Stream<Arguments> invalidStudentDtos() {
        return Stream.of(
                Arguments.of(student(null, 1, true, null, Set.of()), List.of("name")),
                Arguments.of(student(" ", 1, true, null, Set.of()), List.of("name")),
                Arguments.of(student("x".repeat(101), 1, true, null, Set.of()), List.of("name")),
                Arguments.of(student("Alex Morgan", null, true, null, Set.of()), List.of("academicYear")),
                Arguments.of(student("Alex Morgan", 0, true, null, Set.of()), List.of("academicYear")),
                Arguments.of(student("Alex Morgan", 1, null, null, Set.of()), List.of("active")),
                Arguments.of(student("Alex Morgan", 1, true, 0, Set.of()), List.of("profileId")),
                Arguments.of(student("Alex Morgan", 1, true, null, null), List.of("courseIds")),
                Arguments.of(student("Alex Morgan", 1, true, null, Set.of(0)),
                        List.of("courseIds[].<iterable element>")));
    }

    private static StudentDto student(String name, Integer academicYear, Boolean active,
            Integer profileId, Set<Integer> courseIds) {
        return new StudentDto(null, name, academicYear, active, profileId, courseIds);
    }

    /** Confirms every supplied valid DTO has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validStudentDtos")
    public void testValid(StudentDto valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid DTO values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidStudentDtos")
    void testInvalid(StudentDto invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
