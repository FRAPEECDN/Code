package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class FoundationPatternMatchingTest {

    private final FoundationPatternMatching patterns = new FoundationPatternMatching();

    @Test
    public void testPatternMatchingSwitch() {
        assertThat(patterns.describeValue("Java"), equalTo("String:Java"));
        assertThat(patterns.describeValue(25), equalTo("Integer:25"));
        assertThat(patterns.describeValue(null), equalTo("null"));
        assertThat(patterns.describeValue(new Object()), equalTo("Object"));
    }
}