package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FoundationLambdaTest {

    private final FoundationLambda lambda = new FoundationLambda();

    @Test
    public void testMapAndFilterLambdas() {
        assertThat(lambda.mapValues(Arrays.asList(1, 2, 3), value -> value * 2),
            equalTo(Arrays.asList(2, 4, 6)));
        assertThat(lambda.filterValues(Arrays.asList(1, 2, 3, 4), value -> value % 2 == 0),
            equalTo(Arrays.asList(2, 4)));
    }

    @Test
    public void testReduceAndComposition() {
        assertThat(lambda.reduceValues(Arrays.asList(1, 2, 3, 4), 0, Integer::sum), equalTo(10));
        assertThat(lambda.composeFunctions(value -> value + 2, value -> value * 3, 4), equalTo(18));
        assertThat(lambda.createAdder(5).apply(7), equalTo(12));
    }

    @Test
    public void testFormattingLambda() {
        assertThat(lambda.joinFormatted(Arrays.asList(1, 2, 3), value -> "value=" + value),
            equalTo("value=1,value=2,value=3"));
    }
}