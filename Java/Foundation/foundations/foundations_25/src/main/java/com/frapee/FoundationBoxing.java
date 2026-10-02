package com.frapee;

/**
 * Foundation examples for primitive wrapper types, boxing, and unboxing.
 */
public class FoundationBoxing {

    /** @param value primitive boolean to box
     * @return Boolean wrapper */
    public Boolean boxBoolean(boolean value) {
        return value;
    }

    /** @param value primitive byte to box
     * @return Byte wrapper */
    public Byte boxByte(byte value) {
        return value;
    }

    /** @param value primitive short to box
     * @return Short wrapper */
    public Short boxShort(short value) {
        return value;
    }

    /** @param value primitive int to box
     * @return Integer wrapper */
    public Integer boxInt(int value) {
        return value;
    }

    /** @param value primitive long to box
     * @return Long wrapper */
    public Long boxLong(long value) {
        return value;
    }

    /** @param value primitive float to box
     * @return Float wrapper */
    public Float boxFloat(float value) {
        return value;
    }

    /** @param value primitive double to box
     * @return Double wrapper */
    public Double boxDouble(double value) {
        return value;
    }

    /** @param value primitive char to box
     * @return Character wrapper */
    public Character boxChar(char value) {
        return value;
    }

    /** @param value Integer wrapper to unbox
     * @return primitive int value
     * @throws NullPointerException when value is null */
    public int unboxInt(Integer value) {
        return value;
    }

    /** @param value int value to wrap with Integer.valueOf
     * @return Integer wrapper */
    public Integer valueOfInt(int value) {
        return Integer.valueOf(value);
    }

    /** @param value decimal text to parse
     * @return parsed int
     * @throws NumberFormatException when value is not a valid integer */
    public int parseInt(String value) {
        return Integer.parseInt(value);
    }

    /**
     * Compare wrapper contents instead of wrapper references.
     * @param left first Integer
     * @param right second Integer
     * @return true when wrapper values are equal
     */
    public boolean equalIntegerValues(Integer left, Integer right) {
        return left.equals(right);
    }

    /**
     * Demonstrate that small wrapper values may be cached by the JDK.
     * @return whether two valueOf calls for 100 share the cached instance
     */
    public boolean demonstrateIntegerCaching() {
        Integer left = Integer.valueOf(100);
        Integer right = Integer.valueOf(100);
        return left == right;
    }

    /** @return never returns because null unboxing fails
     * @throws NullPointerException always, demonstrating null unboxing */
    public int unboxNull() {
        Integer value = null;
        return value;
    }
}