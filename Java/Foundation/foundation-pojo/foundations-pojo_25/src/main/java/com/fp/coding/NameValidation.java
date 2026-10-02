package com.fp.coding;

import lombok.experimental.UtilityClass;

/** Normalizes nonblank names for use in staff records. */
@UtilityClass
public class NameValidation {
    public static String normalize(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        String normalized = name.strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return normalized;
    }
}
