package com.frapee;

import java.io.IOException;
import java.io.StringReader;

/**
 * Foundation guide to checked, unchecked, and chained exceptions.
 */
public class FoundationExceptions {

    /**
     * Throw a checked exception when requested.
     * @param shouldFail whether to throw the exception
     * @throws IOException when shouldFail is true
     */
    public void checkedFailure(boolean shouldFail) throws IOException {
        if (shouldFail) {
            throw new IOException("Checked failure");
        }
    }

    /**
     * Catch a checked exception and convert it to a status result.
     * @param shouldFail whether the checked operation should fail
     * @return true on success, false when IOException is caught
     */
    public boolean handleCheckedFailure(boolean shouldFail) {
        try {
            checkedFailure(shouldFail);
            return true;
        } catch (IOException exception) {
            return false;
        }
    }

    /**
     * Catch an unchecked parsing exception at an API boundary.
     * @param value text to parse
     * @return parsed value, or -1 when parsing fails
     */
    public int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    /**
     * Demonstrate automatic closing of an AutoCloseable resource.
     * @param value text held by the managed reader
     * @return value after the reader is closed
     * @throws IOException declared resource-operation failure
     */
    public String tryWithResources(String value) throws IOException {
        try (StringReader reader = new StringReader(value)) {
            return value;
        }
    }

    /**
     * Wrap an original exception while preserving it as the cause.
     * @return wrapped exception whose cause is the original IOException
     */
    public IllegalStateException chainException() {
        try {
            throw new IOException("Original failure");
        } catch (IOException exception) {
            return new IllegalStateException("Wrapped failure", exception);
        }
    }
}
