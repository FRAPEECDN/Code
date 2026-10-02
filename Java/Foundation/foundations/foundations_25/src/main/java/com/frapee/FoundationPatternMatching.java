package com.frapee;

/**
 * Foundation example for type patterns in switch expressions.
 */
public class FoundationPatternMatching {

    /**
     * Describe common values with a pattern-matching switch.
     * @param value value to describe, including null
     * @return a description based on the value's type
     */
    public String describeValue(Object value) {
        return switch (value) {
            case null -> "null";
            case String text -> "String:" + text;
            case Integer number -> "Integer:" + number;
            default -> value.getClass().getSimpleName();
        };
    }
}