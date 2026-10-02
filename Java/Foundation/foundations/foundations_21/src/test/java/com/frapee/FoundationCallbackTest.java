package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FoundationCallbackTest {

    @Test
    public void testCustomCallback() {
        FoundationCallback callbacks = new FoundationCallback(new RangeCallbackImplementation());
        assertThat(callbacks.runCallback(), equalTo(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
        assertThat(callbacks.runCallback(3, 5), equalTo(Arrays.asList(3, 4, 5)));
    }

    @Test
    public void testLambdaCanImplementCustomCallback() {
        FoundationCallback callbacks = new FoundationCallback((start, stop) -> Arrays.asList(start, stop));
        assertThat(callbacks.runCallback(2, 8), equalTo(Arrays.asList(2, 8)));
    }
}
