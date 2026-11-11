package com.fp.boot_demo_web.model.validation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.DepartmentEntity;
import com.fp.boot_demo_web.model.enums.DepartmentType;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link DepartmentEntity} persistence constraints. */
@ActiveProfiles("test")
public class TestDepartmentEntityValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies departments with valid names and types. */
    static Stream<DepartmentEntity> validDepartments() {
        return Stream.of(
                department("Arts", DepartmentType.ARTS),
                department("Computer Science", DepartmentType.SCIENCE),
                department("x".repeat(100), DepartmentType.BUSINESS));
    }

    /** Supplies departments and expected property paths for invalid values. */
    static Stream<Arguments> invalidDepartments() {
        return Stream.of(
                Arguments.of(department(null, DepartmentType.SCIENCE), List.of("name")),
                Arguments.of(department(" ", DepartmentType.SCIENCE), List.of("name")),
                Arguments.of(department("x".repeat(101), DepartmentType.SCIENCE), List.of("name")),
                Arguments.of(department("Computer Science", null), List.of("type")));
    }

    private static DepartmentEntity department(String name, DepartmentType type) {
        return DepartmentEntity.builder().id(1).name(name).type(type).courses(Set.of()).build();
    }

    /** Confirms every supplied valid department has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validDepartments")
    public void testValid(DepartmentEntity valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid department values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidDepartments")
    void testInvalid(DepartmentEntity invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
