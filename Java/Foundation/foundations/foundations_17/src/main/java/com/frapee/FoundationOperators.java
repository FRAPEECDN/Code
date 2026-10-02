package com.frapee;

/**
 * Foundation guide to Java operators and evaluation rules.
 * Covers bitwise operations, shifts, compound assignment, precedence, and
 * short-circuit boolean evaluation.
 */
public class FoundationOperators {

    /** @param left left bit pattern
     * @param right right bit pattern
     * @return bitwise AND result */
    public int bitwiseAnd(int left, int right) {
        return left & right;
    }

    /** @param left left bit pattern
     * @param right right bit pattern
     * @return bitwise OR result */
    public int bitwiseOr(int left, int right) {
        return left | right;
    }

    /** @param left left bit pattern
     * @param right right bit pattern
     * @return bitwise XOR result */
    public int bitwiseXor(int left, int right) {
        return left ^ right;
    }

    /** @param value bit pattern
     * @return inverted bit pattern */
    public int bitwiseNot(int value) {
        return ~value;
    }

    /** @param value value to shift
     * @param places number of positions
     * @return left-shifted value */
    public int leftShift(int value, int places) {
        return value << places;
    }

    /** @param value value to shift
     * @param places number of positions
     * @return sign-preserving right-shifted value */
    public int signedRightShift(int value, int places) {
        return value >> places;
    }

    /** @param value value to shift
     * @param places number of positions
     * @return zero-filled right-shifted value */
    public int unsignedRightShift(int value, int places) {
        return value >>> places;
    }

    /** @param value starting value
     * @param amount amount to add
     * @return value after compound addition */
    public int compoundAdd(int value, int amount) {
        value += amount;
        return value;
    }

    /** @return result showing multiplication precedence over addition */
    public int withoutParentheses() {
        return 2 + 3 * 4;
    }

    /** @return result showing parentheses overriding precedence */
    public int withParentheses() {
        return (2 + 3) * 4;
    }

    /**
     * The right side is not evaluated when the left side of &amp;&amp; is false.
     * @param condition left side of the short-circuit expression
     * @param divisor value used by the right side
     * @return result of the boolean expression
     */
    public boolean shortCircuitAnd(boolean condition, int divisor) {
        return condition && 10 / divisor > 0;
    }

    /**
     * The right side is not evaluated when the left side of || is true.
     * @param condition left side of the short-circuit expression
     * @param divisor value used by the right side
     * @return result of the boolean expression
     */
    public boolean shortCircuitOr(boolean condition, int divisor) {
        return condition || 10 / divisor > 0;
    }
}
