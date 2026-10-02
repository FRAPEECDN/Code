package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FoundationPrimitivesTest {

    private final FoundationPrimitives primitives = new FoundationPrimitives();

    @Test
    public void testPrimitiveValues() {
        assertThat(primitives.booleanValue(), equalTo(false));
        assertThat(primitives.byteValue(), equalTo((byte) 10));
        assertThat(primitives.shortValue(), equalTo((short) 128));
        assertThat(primitives.intValue(), equalTo(32768));
        assertThat(primitives.longValue(), equalTo(2147483648L));
        assertThat(primitives.floatValue(), equalTo(343.12f));
        assertThat(primitives.doubleValue(), equalTo(35438580.21473));
        assertThat(primitives.charValue(), equalTo('a'));
    }

    @Test
    public void testPrimitiveLimitsConversionsAndArithmetic() {
        assertThat(primitives.minimumInt(), equalTo(Integer.MIN_VALUE));
        assertThat(primitives.maximumInt(), equalTo(Integer.MAX_VALUE));
        assertThat(primitives.intAsHex(65), equalTo("41"));
        assertThat(primitives.widenByte((byte) 10), equalTo(10L));
        assertThat(primitives.narrowInt(130), equalTo((byte) -126));
        assertThat(primitives.integerOverflow(), equalTo(Integer.MIN_VALUE));
        assertThat(primitives.positiveInfinity(), equalTo(Double.POSITIVE_INFINITY));
        assertTrue(primitives.isNaN(Double.NaN));
        assertThat(primitives.characterCode('a'), equalTo(97));
    }
}