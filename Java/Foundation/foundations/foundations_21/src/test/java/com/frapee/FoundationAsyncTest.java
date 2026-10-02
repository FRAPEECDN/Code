package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FoundationAsyncTest {

    private FoundationAsync fat;
    private final int NUMBER_TEST = 20;
    private final long NUMBER_EXPECTED = 2432902008176640000L;

    @BeforeEach
    public void setupTests() {
        fat = new FoundationAsync();
    }

    @Test
    public void testAsyncThread() {
        long actual = fat.useThread(NUMBER_TEST);
        assertThat(actual, equalTo(NUMBER_EXPECTED));
    }

    @Test
    public void testAsyncVirtualThread() {
        long actual = fat.useVirtualThread(NUMBER_TEST);
        assertThat(actual, equalTo(NUMBER_EXPECTED));
    }    

    @Test
    public void testAsyncVirtualThreadExecutor() {
        long actual = fat.useVirtualThreadExecutor(NUMBER_TEST);
        assertThat(actual, equalTo(NUMBER_EXPECTED));
    }

    @Test
    public void testAsyncTask() {
        Pair<Long, Long> actual = fat.useTask(NUMBER_TEST);
        assertThat(actual.getValue0(), equalTo(0L));
        assertThat(actual.getValue1(), equalTo(NUMBER_EXPECTED));
    }

    @Test
    public void testAsyncCompletable() {
        Pair<Long, Long> actual = fat.useCompletable(NUMBER_TEST);
        assertThat(actual.getValue0(), equalTo(0L));
        assertThat(actual.getValue1(), equalTo(NUMBER_EXPECTED));
    }

    @Test
    public void testFactorialInputValidation() {
        assertThat(fat.useThread(0), equalTo(1L));
        assertThat(fat.useVirtualThread(1), equalTo(1L));
        assertThrows(IllegalArgumentException.class, () -> fat.useThread(-1));
        assertThrows(IllegalArgumentException.class, () -> fat.useVirtualThreadExecutor(21));
    }

    @Test
    public void testCompletableFutureComposition() {
        assertThat(fat.useCompletablePipeline(5), equalTo(120L));
        assertThat(fat.useCompletableCombine(4, 5), equalTo(144L));
        assertThat(fat.useCompletableAll(new int[] {3, 4, 5}), equalTo(new long[] {6L, 24L, 120L}));
    }

    @Test
    public void testConcurrencyCoordination() {
        assertThat(fat.useCountDownLatch(4), equalTo(4));
        assertThat(fat.useAtomicCounter(4, 100), equalTo(400));
        assertThat(fat.useReentrantLock(4, 100), equalTo(400));
        assertThat(fat.useSemaphore(4, 2), equalTo(4));
    }
}
