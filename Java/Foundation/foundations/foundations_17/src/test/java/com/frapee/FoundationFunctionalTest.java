package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class FoundationFunctionalTest {

    private final FoundationFunctional functional = new FoundationFunctional();

    @Test
    public void testStandardFunctionalInterfaces() {
        assertThat(functional.applyFunction(5, value -> value * 2), equalTo(10));
        assertThat(functional.applyBiFunction(5, 2, Integer::sum), equalTo(7));
        assertTrue(functional.testPredicate(4, value -> value % 2 == 0));
        assertFalse(functional.testPredicate(5, value -> value % 2 == 0));
        assertThat(functional.supplyValue(() -> 42), equalTo(42));
    }

    @Test
    public void testConsumerAndSafeExecution() {
        AtomicInteger total = new AtomicInteger();
        functional.acceptNumber(3, total::addAndGet);
        functional.forEachNumber(Arrays.asList(1, 2, 3), total::addAndGet);
        assertThat(total.get(), equalTo(9));
        assertTrue(functional.runSafely(5, total::addAndGet));
        assertFalse(functional.runSafely(5, value -> {
            throw new IllegalStateException("callback failed");
        }));
    }
}
