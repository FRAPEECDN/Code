package com.fp.boot_demo_web.model.validation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.CourseEntity;
import com.fp.boot_demo_web.model.entities.DepartmentEntity;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link CourseEntity} persistence constraints. */
@ActiveProfiles("test")
public class TestCourseEntityValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies courses with valid scalar values and required relationships. */
    static Stream<CourseEntity> validCourses() {
        return Stream.of(
                course("CS101", "Introduction to Computing", 0.0, true, CourseLevel.INTRODUCTORY,
                        department()),
                course("ART20", "Digital Art", 125.50, false, CourseLevel.INTERMEDIATE,
                        department()));
    }

    /** Supplies courses and expected property paths for invalid values. */
    static Stream<Arguments> invalidCourses() {
        return Stream.of(
                Arguments.of(course(null, "Computing", 100.0, true, CourseLevel.INTRODUCTORY, department()),
                        List.of("code")),
                Arguments.of(course("x".repeat(21), "Computing", 100.0, true, CourseLevel.INTRODUCTORY, department()),
                        List.of("code")),
                Arguments.of(course("CS101", null, 100.0, true, CourseLevel.INTRODUCTORY, department()),
                        List.of("title")),
                Arguments.of(course("CS101", "x".repeat(101), 100.0, true, CourseLevel.INTRODUCTORY, department()),
                        List.of("title")),
                Arguments.of(course("CS101", "Computing", null, true, CourseLevel.INTRODUCTORY, department()),
                        List.of("tuition")),
                Arguments.of(course("CS101", "Computing", -1.0, true, CourseLevel.INTRODUCTORY, department()),
                        List.of("tuition")),
                Arguments.of(course("CS101", "Computing", 100.0, null, CourseLevel.INTRODUCTORY, department()),
                        List.of("active")),
                Arguments.of(course("CS101", "Computing", 100.0, true, null, department()),
                        List.of("level")),
                Arguments.of(course("CS101", "Computing", 100.0, true, CourseLevel.INTRODUCTORY, null),
                        List.of("department")));
    }

    private static CourseEntity course(String code, String title, Double tuition, Boolean active,
            CourseLevel level, DepartmentEntity department) {
        return CourseEntity.builder()
                .id(1)
                .code(code)
                .title(title)
                .tuition(tuition)
                .active(active)
                .level(level)
                .department(department)
                .students(Set.of())
                .build();
    }

    private static DepartmentEntity department() {
        return DepartmentEntity.builder().id(1).name("Computer Science").type(DepartmentType.SCIENCE).build();
    }

    /** Confirms every supplied valid course has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validCourses")
    public void testValid(CourseEntity valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid course values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidCourses")
    void testInvalid(CourseEntity invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
