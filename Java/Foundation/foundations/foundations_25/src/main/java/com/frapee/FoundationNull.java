package com.frapee;

import java.util.Objects;

/**
 * Foundation guide to null references and safe null handling.
 */
public class FoundationNull {

    /**
     * Test whether a reference is null.
     * @param value reference to inspect
     * @return true when value is null
     */
    public boolean isNull(String value) { return value == null; }

    /**
     * Supply a fallback for a null reference.
     * @param value value that may be null
     * @param defaultValue fallback value
     * @return value when non-null, otherwise defaultValue
     */
    public String defaultIfNull(String value, String defaultValue) {
        return Objects.requireNonNullElse(value, defaultValue);
    }

    /**
     * Read a length without throwing for null.
     * @param value text that may be null
     * @return text length, or zero for null
     */
    public int safeLength(String value) { return value == null ? 0 : value.length(); }

    /**
     * Enforce a non-null precondition.
     * @param value required reference
     * @return the same non-null value
     * @throws NullPointerException when value is null
     */
    public String requireValue(String value) {
        return Objects.requireNonNull(value, "Value must not be null");
    }
}
