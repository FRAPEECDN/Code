package com.fp.boot_demo_web.model.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * MongoDB document for a simple record stored in the {@code simple} collection.
 *
 * <p>MongoDB generates the document identifier. The record name is required
 * and is limited to 50 characters.</p>
 */
@Document(collection = "simple")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SimpleEntity {

    private static final int MAX_FIELD_LENGTH = 50;

    /** MongoDB-generated document identifier, represented as an ObjectId string. */
    @Id
    private String id;

    /** Required record name, limited to {@value #MAX_FIELD_LENGTH} characters. */
    @Size(max = MAX_FIELD_LENGTH)
    @NotNull(message = "Name cannot be null")
    private String name;

}
