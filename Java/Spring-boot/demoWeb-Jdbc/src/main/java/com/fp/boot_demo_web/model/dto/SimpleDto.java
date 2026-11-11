package com.fp.boot_demo_web.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * API representation of a simple record.
 *
 * @param id record identifier, or {@code 0} when creating a record
 * @param name record name; must contain 3 to 50 non-blank characters
 */
public record SimpleDto(

        @Schema(description = "Simple record id (0 for new record)", example = "1") int id,

        @Schema(description = "Simple record name", example = "apple") @NotBlank(message = "Name cannot be null or blank") @Size(min = 3, max = 50, message = "Name can only be between 3 and 50 characters long") String name

) {
}
