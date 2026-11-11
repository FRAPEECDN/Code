package com.fp.boot_demo_web.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity for a simple record persisted in the {@code simple} table.
 *
 * <p>The database generates the identifier. The record name is required and is
 * limited to 50 characters.</p>
 */
@Entity
@Table(name = "simple")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleEntity {

    private static final int MAX_FIELD_LENGTH = 50;

    /** Database-generated primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Required record name, limited to {@value #MAX_FIELD_LENGTH} characters. */
    @Column(name = "name", nullable = false, length = MAX_FIELD_LENGTH)
    @Size(max = MAX_FIELD_LENGTH)
    @NotNull(message = "Name cannot be null")
    private String name;

}
