package com.fp.boot_demo_web.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * API representation of a student's profile. The student relationship is
 * represented by ID to avoid a recursive DTO graph.
 *
 * @param id generated profile identifier; may be {@code null} when creating
 * @param email student email address
 * @param phone optional phone number
 * @param studentId ID of the student who owns the profile
 */
public record StudentProfileDto(

        @Schema(description = "Generated profile ID; omit or use null when creating", example = "1", nullable = true)
        Integer id,

        @Schema(description = "Student email address", example = "alex@example.com", format = "email", maxLength = 150)
        @NotBlank @Email @Size(max = 150) String email,

        @Schema(description = "Optional phone number", example = "+1-555-0100", maxLength = 30, nullable = true)
        @Size(max = 30) String phone,

        @Schema(description = "Owning student ID", example = "1", minimum = "1")
        @NotNull @Positive Integer studentId

) {
}
