package com.fp.coding;

import java.util.Arrays;

/**
 * Enum example that associates each constant with a stable, human-readable string.
 *
 * <p>{@link #fromValue(String)} demonstrates converting external text into a typed
 * enum value without relying on declaration ordinals.
 */
public enum StringEnum {
    /** Positive mood mapped to {@code "happy"}. */
    HAPPY("happy"),
    /** Sad mood mapped to {@code "sad"}. */
    SAD("sad"),
    /** Angry mood mapped to {@code "mad"}. */
    MAD("mad"),
    /** Neutral mood mapped to {@code "ok"}. */
    OK("ok"),
    /** Relaxed mood mapped to {@code "chill"}. */
    CHILL("chill");

    private final String value;

    StringEnum(String value) {
        this.value = value;
    }

    /** @return the stable string value associated with this constant */
    public String getValue() {
        return value;
    }

    /**
     * Finds a constant whose stored value matches the supplied text, ignoring case.
     *
     * @param value text to look up
     * @return the matching enum constant
     * @throws IllegalArgumentException if no constant matches, or {@code value} is null
     */
    public static StringEnum fromValue(String value) {
        return Arrays.stream(values())
                .filter(item -> item.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown mood: " + value));
    }
}