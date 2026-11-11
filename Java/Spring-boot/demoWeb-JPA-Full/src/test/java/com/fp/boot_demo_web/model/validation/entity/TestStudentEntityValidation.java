package com.fp.boot_demo_web.model.validation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.StudentEntity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link StudentEntity} persistence constraints. */
@ActiveProfiles("test")
public class TestStudentEntityValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies students with valid names, years, and active states. */
    static Stream<StudentEntity> validStudents() {
        return Stream.of(
                student("Alex Morgan", 1, true),
                student("Jordan Lee", 5, false));
    }

    /** Supplies students and expected property paths for invalid values. */
    static Stream<Arguments> invalidStudents() {
        return Stream.of(
                Arguments.of(student(null, 1, true), List.of("name")),
                Arguments.of(student(" ", 1, true), List.of("name")),
                Arguments.of(student("x".repeat(101), 1, true), List.of("name")),
                Arguments.of(student("Alex Morgan", null, true), List.of("academicYear")),
                Arguments.of(student("Alex Morgan", 0, true), List.of("academicYear")),
                Arguments.of(student("Alex Morgan", 1, null), List.of("active")));
    }

    private static StudentEntity student(String name, Integer academicYear, Boolean active) {
        return StudentEntity.builder()
                .id(1)
                .name(name)
                .academicYear(academicYear)
                .active(active)
                .profile(null)
                .courses(Set.of())
                .build();
    }

    /** Confirms every supplied valid student has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validStudents")
    public void testValid(StudentEntity valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid student values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidStudents")
    void testInvalid(StudentEntity invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
