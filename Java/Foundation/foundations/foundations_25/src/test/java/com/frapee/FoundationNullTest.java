package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FoundationNullTest {

    private final FoundationNull nulls = new FoundationNull();

    @Test
    public void testNullHandling() {
        assertThat(nulls.isNull(null), equalTo(true));
        assertThat(nulls.defaultIfNull(null, "default"), equalTo("default"));
        assertThat(nulls.defaultIfNull("value", "default"), equalTo("value"));
        assertThat(nulls.safeLength(null), equalTo(0));
        assertThat(nulls.safeLength("hello"), equalTo(5));
        assertThrows(NullPointerException.class, () -> nulls.requireValue(null));
    }
}