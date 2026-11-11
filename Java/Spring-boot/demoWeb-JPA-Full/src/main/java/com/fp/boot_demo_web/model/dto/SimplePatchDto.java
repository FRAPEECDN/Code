package com.fp.boot_demo_web.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request body for changing the mutable name field of a simple record. */
public record SimplePatchDto(

        @Schema(description = "Replacement record name", example = "pear", minLength = 3, maxLength = 50)
        @NotBlank(message = "Name cannot be null or blank")
        @Size(min = 3, max = 50, message = "Name can only be between 3 and 50 characters long")
        String name

) {
}
