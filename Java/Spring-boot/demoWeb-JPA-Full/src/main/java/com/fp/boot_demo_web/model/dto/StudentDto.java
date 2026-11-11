package com.fp.boot_demo_web.model.dto;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * API representation of a student. Profile and course relationships are
 * represented by IDs to avoid recursive DTO graphs.
 *
 * @param id generated student identifier; may be {@code null} when creating
 * @param name student name
 * @param academicYear current academic year
 * @param active whether the student is active
 * @param profileId optional ID of the student's existing profile; profile changes use profile routes
 * @param courseIds IDs of the student's current courses; create/update may assign existing courses
 */
public record StudentDto(

        @Schema(description = "Generated student ID; omit or use null when creating", example = "1", nullable = true)
        Integer id,

        @Schema(description = "Student name", example = "Alex Morgan", minLength = 1, maxLength = 100)
        @NotBlank @Size(max = 100) String name,

        @Schema(description = "Current academic year", example = "2", minimum = "1")
        @NotNull @Min(1) Integer academicYear,

        @Schema(description = "Whether the student is active", example = "true")
        @NotNull Boolean active,

        @Schema(description = "Existing profile ID; profile changes must use the profile routes",
                example = "1", minimum = "1", nullable = true)
        @Positive Integer profileId,

        @Schema(description = "IDs of current courses; create/update may set these IDs")
        @NotNull Set<@NotNull @Positive Integer> courseIds

) {
}
