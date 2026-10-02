package com.frapee;

/**
 * Foundation examples for built-in Java annotations.
 */
public class FoundationAnnotations {

    /**
     * Deprecated marks an API that should no longer be used for new code.
     */
    @Deprecated(since = "1.0", forRemoval = false)
    public String deprecatedExample() {
        return "deprecated";
    }

    /**
     * SuppressWarnings documents an intentional compiler-warning suppression.
     */
    @SuppressWarnings("unused")
    public String suppressedWarningExample() {
        return "suppressed";
    }
}