package com.fp.boot_demo_web.model.validation.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.dto.DepartmentDto;
import com.fp.boot_demo_web.model.enums.DepartmentType;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link DepartmentDto} input constraints. */
@ActiveProfiles("test")
public class TestDepartmentDtoValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies DTOs with valid department values. */
    static Stream<DepartmentDto> validDepartmentDtos() {
        return Stream.of(
                new DepartmentDto(null, "Arts", DepartmentType.ARTS, Set.of()),
                new DepartmentDto(1, "Computer Science", DepartmentType.SCIENCE, Set.of(2)));
    }

    /** Supplies DTOs and expected property paths for invalid values. */
    static Stream<Arguments> invalidDepartmentDtos() {
        return Stream.of(
                Arguments.of(new DepartmentDto(null, null, DepartmentType.SCIENCE, Set.of()), List.of("name")),
                Arguments.of(new DepartmentDto(null, " ", DepartmentType.SCIENCE, Set.of()), List.of("name")),
                Arguments.of(new DepartmentDto(null, "x".repeat(101), DepartmentType.SCIENCE, Set.of()),
                        List.of("name")),
                Arguments.of(new DepartmentDto(null, "Arts", null, Set.of()), List.of("type")),
                Arguments.of(new DepartmentDto(null, "Arts", DepartmentType.ARTS, null), List.of("courseIds")),
                Arguments.of(new DepartmentDto(null, "Arts", DepartmentType.ARTS, Set.of(0)),
                        List.of("courseIds[].<iterable element>")));
    }

    /** Confirms every supplied valid DTO has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validDepartmentDtos")
    public void testValid(DepartmentDto valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid DTO values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("invalidDepartmentDtos")
    void testInvalid(DepartmentDto invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }
}
