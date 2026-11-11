package com.fp.boot_demo_web.model.validation.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ActiveProfiles;

import com.fp.boot_demo_web.model.entities.SimpleEntity;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** Validation tests for {@link SimpleEntity} row-model constraints. */
@ActiveProfiles("test")
public class TestSimpleEntityValidation {

    static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /** Supplies entities with valid names. */
    static Stream<SimpleEntity> validSimpleEntities() {
        return Stream.of(
                SimpleEntity.builder().id(0).name("a").build(),
                SimpleEntity.builder().id(1).name("abc").build(),
                SimpleEntity.builder().id(2).name("normal range").build(),
                SimpleEntity.builder().id(3).name("abcdefhijklmnopqrstuvwxyzabcdefhijklmnopqrstuvwx").build());
    }

    /** Supplies entities and expected property paths for invalid values. */
    static Stream<Arguments> inValidSimpleEntities() {
        return Stream.of(
                // Arguments.of(SimpleEntity.builder().id(0).name("").build(), List.of("name")),
                Arguments.of(SimpleEntity.builder().id(1).name(null).build(), List.of("name")),
                Arguments.of(SimpleEntity.builder().id(2)
                        .name("abcdefhijklmnopqrstuvwxyzabcdefhijklmnopqrstuvwxyzabcdef").build(), List.of("name")));
    }

    /** Confirms every supplied valid entity has no constraint violations. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("validSimpleEntities")
    public void testValid(SimpleEntity valid) {
        assertThat(validator.validate(valid)).isEmpty();
    }

    /** Confirms invalid entity values report the expected properties. */
    @ParameterizedTest(name = "{0} invalid {1}")
    @MethodSource("inValidSimpleEntities")
    void testInvalid(SimpleEntity invalid, List<String> invalidPaths) {
        assertThat(validator.validate(invalid))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrderElementsOf(invalidPaths);
    }

}
