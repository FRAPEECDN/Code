package com.fp.boot_demo_web.model.dto;

import java.util.Set;

import com.fp.boot_demo_web.model.enums.CourseLevel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * API representation of a course. Department and student relationships are
 * represented by IDs to avoid recursive DTO graphs.
 *
 * @param id generated course identifier; may be {@code null} when creating
 * @param code unique course code
 * @param title course title
 * @param tuition non-negative tuition amount
 * @param active whether the course is active
 * @param level course difficulty
 * @param departmentId ID of the owning department
 * @param studentIds current registration IDs; empty on create and unchanged by course updates
 */
public record CourseDto(

        @Schema(description = "Generated course ID; omit or use null when creating", example = "1", nullable = true)
        Integer id,

        @Schema(description = "Unique course code", example = "CS101", minLength = 1, maxLength = 20)
        @NotBlank @Size(max = 20) String code,

        @Schema(description = "Course title", example = "Introduction to Computing", minLength = 1, maxLength = 100)
        @NotBlank @Size(max = 100) String title,

        @Schema(description = "Tuition amount", example = "125.50", minimum = "0")
        @NotNull @PositiveOrZero Double tuition,

        @Schema(description = "Whether the course is active", example = "true")
        @NotNull Boolean active,

        @Schema(description = "Course difficulty", example = "INTRODUCTORY")
        @NotNull CourseLevel level,

        @Schema(description = "Owning department ID", example = "1", minimum = "1")
        @NotNull @Positive Integer departmentId,

        @Schema(description = "Current registered student IDs; must be empty on create and unchanged on course update")
        @NotNull Set<@NotNull @Positive Integer> studentIds

) {
}
