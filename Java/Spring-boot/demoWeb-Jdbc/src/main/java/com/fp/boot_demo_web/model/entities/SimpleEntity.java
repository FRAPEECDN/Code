package com.fp.boot_demo_web.model.entities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JDBC row model for a simple record persisted in the {@code simple} table.
 *
 * <p>The database generates the identifier. The record name is required and is
 * limited to 50 characters.</p>
 */
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleEntity {

    private static final int MAX_FIELD_LENGTH = 50;

    private int id;

    /** Required record name, limited to {@value #MAX_FIELD_LENGTH} characters. */
    @Size(max = MAX_FIELD_LENGTH)
    @NotNull(message = "Name cannot be null")
    private String name;

}
