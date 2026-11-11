package com.fp.boot_demo_web.model.dto;

import java.util.Set;

import com.fp.boot_demo_web.model.enums.DepartmentType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * API representation of a department. Related courses are represented by ID
 * to avoid recursive DTO graphs.
 *
 * @param id generated department identifier; may be {@code null} when creating
 * @param name department name
 * @param type department classification
 * @param courseIds IDs of existing courses; create requires an empty set and update cannot change this set
 */
public record DepartmentDto(

        @Schema(description = "Generated department ID; omit or use null when creating", example = "1", nullable = true)
        Integer id,

        @Schema(description = "Department name", example = "Computer Science", minLength = 1, maxLength = 100)
        @NotBlank @Size(max = 100) String name,

        @Schema(description = "Department classification", example = "SCIENCE")
        @NotNull DepartmentType type,

        @Schema(description = "IDs of the department's courses; empty on create and unchanged by department updates")
        @NotNull Set<@NotNull @Positive Integer> courseIds

) {
}
