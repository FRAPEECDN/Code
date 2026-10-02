package com.frapee;

/**
 * Java 25 preview examples for exact primitive type patterns and conversions.
 */
public class FoundationPrimitivePatterns {

    /**
     * Check whether an int can be converted to byte without losing information.
     * @param value value to check
     * @return true when value is in the byte range
     */
    public boolean fitsInByte(int value) {
        return value instanceof byte;
    }

    /**
     * Convert an int to byte only when the conversion is exact.
     * @param value value to convert
     * @return exact byte value
     * @throws IllegalArgumentException when value is outside the byte range
     */
    public byte narrowExactlyToByte(int value) {
        if (value instanceof byte narrowedValue) {
            return narrowedValue;
        }
        throw new IllegalArgumentException("Value is outside the byte range");
    }
}