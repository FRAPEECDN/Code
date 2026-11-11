package com.fp.boot_demo_web.model.validation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.StudentEntity;
import com.fp.boot_demo_web.model.entities.StudentProfileEntity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link StudentProfileEntity} persistence constraints. */
@ActiveProfiles("test")
public class TestStudentProfileEntityValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies profiles with valid email, phone, and student values. */
    static Stream<StudentProfileEntity> validProfiles() {
        return Stream.of(
                profile("alex@example.com", null, student()),
                profile("jordan.lee@example.com", "+1-555-0100", student()));
    }

    /** Supplies profiles and expected property paths for invalid values. */
    static Stream<Arguments> invalidProfiles() {
        return Stream.of(
                Arguments.of(profile(null, null, student()), List.of("email")),
                Arguments.of(profile(" ", null, student()), List.of("email", "email")),
                Arguments.of(profile("not-an-email", null, student()), List.of("email")),
                Arguments.of(profile("x".repeat(140) + "@example.com", null, student()),
                        List.of("email", "email")),
                Arguments.of(profile("alex@example.com", "x".repeat(31), student()), List.of("phone")),
                Arguments.of(profile("alex@example.com", null, null), List.of("student")));
    }

    private static StudentProfileEntity profile(String email, String phone, StudentEntity student) {
        return StudentProfileEntity.builder()
                .id(1)
                .email(email)
                .phone(phone)
                .student(student)
                .build();
    }

    private static StudentEntity student() {
        return StudentEntity.builder().id(1).name("Alex Morgan").academicYear(1).active(true).build();
    }

    /** Confirms every supplied valid profile has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validProfiles")
    public void testValid(StudentProfileEntity valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid profile values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidProfiles")
    void testInvalid(StudentProfileEntity invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
