package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class FoundationExceptionsTest {

    private final FoundationExceptions exceptions = new FoundationExceptions();

    @Test
    public void testCheckedAndUncheckedHandling() throws IOException {
        exceptions.checkedFailure(false);
        assertThrows(IOException.class, () -> exceptions.checkedFailure(true));
        assertThat(exceptions.handleCheckedFailure(false), equalTo(true));
        assertThat(exceptions.handleCheckedFailure(true), equalTo(false));
        assertThat(exceptions.parseInteger("42"), equalTo(42));
        assertThat(exceptions.parseInteger("invalid"), equalTo(-1));
    }

    @Test
    public void testResourcesAndExceptionCause() throws IOException {
        assertThat(exceptions.tryWithResources("text"), equalTo("text"));
        assertThat(exceptions.chainException().getCause().getMessage(), equalTo("Original failure"));
    }
}