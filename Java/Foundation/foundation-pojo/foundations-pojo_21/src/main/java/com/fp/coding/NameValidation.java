package com.fp.coding;

/** Normalizes nonblank names for use in staff records. */
public final class NameValidation {
    private NameValidation() {
    }

    /**
     * Strips surrounding whitespace and rejects a missing or blank name.
     *
     * @param name candidate name
     * @return normalized nonblank name
     * @throws IllegalArgumentException if {@code name} is null or blank
     */
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
