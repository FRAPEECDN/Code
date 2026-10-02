package com.frapee;

/**
 * Foundation guide to String values and StringBuilder.
 * Demonstrates immutable String operations, mutable text building, text blocks,
 * searching, slicing, replacement, and splitting.
 */
public class FoundationStrings {

    /**
     * Compare String contents rather than object references.
     * @param left first string
     * @param right second string
     * @return true when contents match
     */
    public boolean equalByValue(String left, String right) {
        return left.equals(right);
    }

    /**
     * Concatenate text into a new String value.
     * @param left first text
     * @param right text to append
     * @return newly concatenated String
     */
    public String concatenate(String left, String right) {
        return left + right;
    }

    /**
     * Build repeated text with a mutable StringBuilder.
     * @param first initial text
     * @param second text to append
     * @param number numeric text to append
     * @return built text
     */
    public String buildText(String first, String second, int number) {
        StringBuilder builder = new StringBuilder(first);
        builder.append(':').append(second).append(':').append(number);
        return builder.toString();
    }

    /** @param value text to measure
     * @return UTF-16 code-unit length */
    public int lengthOf(String value) {
        return value.length();
    }

    /** @param value text to inspect
     * @param index zero-based character index
     * @return character at index */
    public char characterAt(String value, int index) {
        return value.charAt(index);
    }

    /** @param value text to search
     * @param search text to find
     * @return true when search occurs in value */
    public boolean contains(String value, String search) {
        return value.contains(search);
    }

    /** @param value source text
     * @param fromIndex inclusive start
     * @param toIndex exclusive end
     * @return selected substring */
    public String substring(String value, int fromIndex, int toIndex) {
        return value.substring(fromIndex, toIndex);
    }

    /** @param value source text
     * @param target text to replace
     * @param replacement replacement text
     * @return new text with replacements */
    public String replace(String value, String target, String replacement) {
        return value.replace(target, replacement);
    }

    /** @param value source text
     * @param delimiter regular-expression delimiter
     * @return split text parts */
    public String[] split(String value, String delimiter) {
        return value.split(delimiter);
    }

    /** @return a Java text block containing two lines */
    public String textBlock() {
        return """
                first line
                second line
                """;
    }

    /** @param value text to trim and convert
     * @return trimmed uppercase text */
    public String trimAndUppercase(String value) {
        return value.trim().toUpperCase();
    }
}
