package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class FoundationMethodsTest {

    private final FoundationMethods methods = new FoundationMethods();

    @Test
    public void testVarargsAndOverloading() {
        assertThat(methods.sum(), equalTo(0));
        assertThat(methods.sum(1, 2, 3), equalTo(6));
        assertThat(methods.describe(5), equalTo("int:5"));
        assertThat(methods.describe(5.5), equalTo("double:5.5"));
        assertThat(methods.describe("five"), equalTo("String:five"));
        assertThat(methods.useFallbackValue(0, 10), equalTo(10));
    }
}