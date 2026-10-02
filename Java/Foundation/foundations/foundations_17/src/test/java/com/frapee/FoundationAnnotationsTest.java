package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class FoundationAnnotationsTest {

    private final FoundationAnnotations annotations = new FoundationAnnotations();

    @Test
    public void testAnnotatedExamples() {
        assertThat(annotations.deprecatedExample(), equalTo("deprecated"));
        assertThat(annotations.suppressedWarningExample(), equalTo("suppressed"));
    }
}