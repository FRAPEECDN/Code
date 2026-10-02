package com.frapee;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test-only example using JUnit 5 assertions without Hamcrest.
 */
public class FoundationJUnit5Test {

    private FoundationBasics basics;
    private FoundationArray arrays;

    @BeforeEach
    void setUp() {
        basics = new FoundationBasics();
        arrays = new FoundationArray();
    }

    @Test
    @DisplayName("JUnit assertions verify control flow and arrays")
    void testValuesAndArrays() {
        assertEquals(-1, basics.ifOnly(1, 2));
        assertFalse(basics.logicalAnd(true, false));
        assertTrue(basics.logicalXor(true, false));
        assertArrayEquals(new int[] {1, 2, 3},
            arrays.arraysCopyOfRange(new int[] {0, 1, 2, 3}, 1, 4));
    }

    @Test
    void testObjectStateAndExceptions() {
        int[] values = {3, 1, 2};
        arrays.arraysParallelSort(values);
        assertArrayEquals(new int[] {1, 2, 3}, values);
        assertNotNull(values);
        assertThrows(IllegalArgumentException.class, () -> basics.recursiveFactorial(-1));
    }
}
