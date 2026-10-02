package com.frapee;

/**
 * Foundation guide to method arguments, varargs, and overload resolution.
 * Java has no default arguments; use overloads or an explicit fallback value.
 */
public class FoundationMethods {

    /**
     * Add zero or more values using a varargs parameter.
     * @param values values to add
     * @return sum of all values
     */
    public int sum(int... values) {
        int result = 0;
        for (int value : values) {
            result += value;
        }
        return result;
    }

    /** @param value integer to describe
     * @return description selected by overload resolution */
    public String describe(int value) {
        return "int:" + value;
    }

    /** @param value decimal value to describe
     * @return description selected by overload resolution */
    public String describe(double value) {
        return "double:" + value;
    }

    /** @param value text to describe
     * @return description selected by overload resolution */
    public String describe(String value) {
        return "String:" + value;
    }

    /**
     * Select a fallback explicitly when the preferred value is zero.
     * @param value preferred value
     * @param fallback value used when value is zero
     * @return value when non-zero, otherwise fallback
     */
    public int useFallbackValue(int value, int fallback) {
        return value == 0 ? fallback : value;
    }
}
