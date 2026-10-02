package com.fp.coding;

/**
 * Simple enum used to demonstrate declaration order and {@link #ordinal()}.
 *
 * <p>An ordinal is only a zero-based position in this declaration; it should not
 * be persisted or treated as a stable business identifier.
 */
public enum OrdinalEnum {
    /** First value in declaration order. */
    FIRST,
    /** Second value in declaration order. */
    SECOND,
    /** Third value in declaration order. */
    THIRD,
    /** Fourth value in declaration order. */
    FOURTH,
    /** Fifth value in declaration order. */
    FIFTH
}