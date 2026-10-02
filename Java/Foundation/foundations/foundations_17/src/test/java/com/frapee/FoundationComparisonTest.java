package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FoundationComparisonTest {

    private final FoundationComparison comparison = new FoundationComparison();

    @Test
    public void testComparableAndIntegerComparison() {
        assertThat(comparison.compareIntegers(2, 5) < 0, equalTo(true));
        assertThat(comparison.compareIntegers(5, 2) > 0, equalTo(true));
        assertThat(comparison.compareIntegers(5, 5), equalTo(0));
        assertThat(comparison.sortNaturally(Arrays.asList("pear", "apple", "orange")),
            equalTo(Arrays.asList("apple", "orange", "pear")));
    }

    @Test
    public void testComparatorOrdering() {
        assertThat(comparison.sortByLength(Arrays.asList("four", "a", "three")),
            equalTo(Arrays.asList("a", "four", "three")));
        assertThat(comparison.sortByLengthThenName(Arrays.asList("pear", "kiwi", "apple", "fig")),
            equalTo(Arrays.asList("fig", "kiwi", "pear", "apple")));
        assertThat(comparison.compareWith(2, 5, (left, right) -> right - left) > 0, equalTo(true));
    }
}