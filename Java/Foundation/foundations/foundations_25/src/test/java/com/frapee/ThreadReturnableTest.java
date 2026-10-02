package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class ThreadReturnableTest {

    @Test
    public void testRunnableCalculatesFactorial() {
        ThreadReturnable runnable = new ThreadReturnable();
        runnable.setNumber(5);
        runnable.run();
        assertThat(runnable.getValue(), equalTo(120L));
    }

    @Test
    public void testRunnableHandlesZero() {
        ThreadReturnable runnable = new ThreadReturnable();
        runnable.setNumber(0);
        runnable.run();
        assertThat(runnable.getValue(), equalTo(1L));
    }
}
