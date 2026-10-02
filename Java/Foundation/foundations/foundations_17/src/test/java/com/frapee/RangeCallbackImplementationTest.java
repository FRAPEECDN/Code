package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class RangeCallbackImplementationTest {

    @Test
    public void testRangeGeneration() {
        RangeCallback callback = new RangeCallbackImplementation();
        assertThat(callback.generateList(2, 5), equalTo(Arrays.asList(2, 3, 4, 5)));
        assertThat(callback.generateList(4, 4), equalTo(Arrays.asList(4)));
    }
}
