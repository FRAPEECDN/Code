package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * Test-only example using Hamcrest matchers without JUnit assertion methods.
 */
public class FoundationHamcrestTest {

    private final FoundationBasics basics = new FoundationBasics();
    private final FoundationArray arrays = new FoundationArray();

    @Test
    void testValueAndCollectionMatchers() {
        assertThat(basics.ifMulti(1, 2), equalTo(-1));
        assertThat(Arrays.asList(1, 2, 3), hasItems(1, 3));
        assertThat("Java Foundation", containsString("Java"));
        assertThat(arrays.arraysToString(arrays.arraysCopyOf(new int[] {1, 2}, 3)),
            equalTo("[1, 2, 0]"));
    }

    @Test
    void testCompositionAndTypeMatchers() {
        assertThat(10, allOf(greaterThan(5), lessThan(20)));
        assertThat(3.14, closeTo(3.1, 0.1));
        assertThat("Foundation", containsString("Found"));
        assertThat("Java", not(nullValue()));
        assertThat(new IllegalArgumentException(), instanceOf(RuntimeException.class));
    }
}
