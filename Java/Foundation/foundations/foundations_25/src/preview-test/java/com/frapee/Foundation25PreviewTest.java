package com.frapee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class Foundation25PreviewTest {

    @Test
    public void testStructuredConcurrencyCombinesFactorials() throws InterruptedException {
        assertEquals(144L, new FoundationStructuredConcurrency().combineFactorials(4, 5));
    }

    @Test
    public void testPrimitivePatternsRequireExactConversion() {
        FoundationPrimitivePatterns patterns = new FoundationPrimitivePatterns();
        assertTrue(patterns.fitsInByte(Byte.MIN_VALUE));
        assertTrue(patterns.fitsInByte(Byte.MAX_VALUE));
        assertFalse(patterns.fitsInByte(Byte.MIN_VALUE - 1));
        assertFalse(patterns.fitsInByte(Byte.MAX_VALUE + 1));
        assertEquals((byte) 127, patterns.narrowExactlyToByte(127));
        assertThrows(IllegalArgumentException.class, () -> patterns.narrowExactlyToByte(128));
    }
}