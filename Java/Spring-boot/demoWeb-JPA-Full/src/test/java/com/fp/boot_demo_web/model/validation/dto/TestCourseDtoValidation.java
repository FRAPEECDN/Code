package com.fp.boot_demo_web.model.validation.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.dto.CourseDto;
import com.fp.boot_demo_web.model.enums.CourseLevel;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link CourseDto} input constraints. */
@ActiveProfiles("test")
public class TestCourseDtoValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies DTOs with valid course values and relationship IDs. */
    static Stream<CourseDto> validCourseDtos() {
        return Stream.of(
                course("CS101", "Introduction to Computing", 0.0, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                course("ART20", "Digital Art", 125.50, false, CourseLevel.INTERMEDIATE, 2, Set.of(3)));
    }

    /** Supplies DTOs and expected property paths for invalid values. */
    static Stream<Arguments> invalidCourseDtos() {
        return Stream.of(
                Arguments.of(course(null, "Computing", 100.0, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("code")),
                Arguments.of(course("x".repeat(21), "Computing", 100.0, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("code")),
                Arguments.of(course("CS101", null, 100.0, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("title")),
                Arguments.of(course("CS101", "x".repeat(101), 100.0, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("title")),
                Arguments.of(course("CS101", "Computing", null, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("tuition")),
                Arguments.of(course("CS101", "Computing", -1.0, true, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("tuition")),
                Arguments.of(course("CS101", "Computing", 100.0, null, CourseLevel.INTRODUCTORY, 1, Set.of()),
                        List.of("active")),
                Arguments.of(course("CS101", "Computing", 100.0, true, null, 1, Set.of()), List.of("level")),
                Arguments.of(course("CS101", "Computing", 100.0, true, CourseLevel.INTRODUCTORY, null, Set.of()),
                        List.of("departmentId")),
                Arguments.of(course("CS101", "Computing", 100.0, true, CourseLevel.INTRODUCTORY, 0, Set.of()),
                        List.of("departmentId")),
                Arguments.of(course("CS101", "Computing", 100.0, true, CourseLevel.INTRODUCTORY, 1, null),
                        List.of("studentIds")),
                Arguments.of(course("CS101", "Computing", 100.0, true, CourseLevel.INTRODUCTORY, 1, Set.of(0)),
                        List.of("studentIds[].<iterable element>")));
    }

    private static CourseDto course(String code, String title, Double tuition, Boolean active,
            CourseLevel level, Integer departmentId, Set<Integer> studentIds) {
        return new CourseDto(null, code, title, tuition, active, level, departmentId, studentIds);
    }

    /** Confirms every supplied valid DTO has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validCourseDtos")
    public void testValid(CourseDto valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid DTO values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidCourseDtos")
    void testInvalid(CourseDto invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
