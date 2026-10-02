package com.frapee;

/**
 * Foundation guide to Java primitive values and primitive conversions.
 */
public class FoundationPrimitives {

    /** @return a primitive boolean value */
    public boolean booleanValue() { return false; }

    /** @return a primitive byte value */
    public byte byteValue() { return 10; }

    /** @return a primitive short value showing a widened byte boundary */
    public short shortValue() { return (short) Byte.MAX_VALUE + 1; }

    /** @return a primitive int value showing a widened short boundary */
    public int intValue() { return (int) Short.MAX_VALUE + 1; }

    /** @return a primitive long value showing a widened int boundary */
    public long longValue() { return (long) Integer.MAX_VALUE + 1; }

    /** @return a primitive float value */
    public float floatValue() { return 343.12f; }

    /** @return a primitive double value */
    public double doubleValue() { return 35438580.21473; }

    /** @return a primitive character value */
    public char charValue() { return 'a'; }

    /** @return the smallest value representable by an int */
    public int minimumInt() { return Integer.MIN_VALUE; }

    /** @return the largest value representable by an int */
    public int maximumInt() { return Integer.MAX_VALUE; }

    /** @param value integer to convert
     * @return hexadecimal representation of value */
    public String intAsHex(int value) { return Integer.toHexString(value); }

    /**
     * Widen a byte to long without an explicit cast.
     * @param value byte to widen
     * @return widened value
     */
    public long widenByte(byte value) { return value; }

    /**
     * Narrow an int to byte; values outside the byte range may be truncated.
     * @param value int to narrow
     * @return narrowed value
     */
    public byte narrowInt(int value) { return (byte) value; }

    /**
     * Demonstrate integer wraparound beyond the type range.
     * @return Integer.MIN_VALUE after overflow
     */
    public int integerOverflow() { return Integer.MAX_VALUE + 1; }

    /** @return positive floating-point infinity */
    public double positiveInfinity() { return Double.POSITIVE_INFINITY; }

    /** @param value floating-point value to inspect
     * @return true when value is NaN */
    public boolean isNaN(double value) { return Double.isNaN(value); }

    /** @param value character to convert
     * @return numeric Unicode code point value */
    public int characterCode(char value) { return value; }
}
