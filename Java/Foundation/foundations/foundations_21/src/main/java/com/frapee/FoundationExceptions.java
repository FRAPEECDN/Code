package com.frapee;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;

/**
 * Foundation guide to checked, unchecked, chained exceptions, and resource
 * cleanup.
 *
 * <p>
 * {@link IOException} is checked: {@link #checkedFailure(boolean)} declares it
 * and callers must catch or declare
 * it. {@link NumberFormatException} is unchecked, so
 * {@link #parseInteger(String)} can catch it without declaring
 * it. {@link #tryWithResources(String)} reads from a managed reader and closes
 * it automatically.
 */
public class FoundationExceptions {

    /**
     * Performs an operation whose failure is reported as a checked exception.
     *
     * <p>
     * The {@code throws IOException} declaration requires callers to choose whether
     * to catch the failure or
     * propagate it to their own callers.
     * 
     * @param shouldFail whether to throw the exception
     * @throws IOException when shouldFail is true
     */
    public void checkedFailure(boolean shouldFail) throws IOException {
        if (shouldFail) {
            throw new IOException("Checked failure");
        }
    }

    /**
     * Catches a checked exception and converts it to a status result.
     *
     * <p>
     * This illustrates handling at an API boundary. Returning only a boolean
     * intentionally discards the cause;
     * production code should preserve diagnostic details when callers need to react
     * to them.
     * 
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
     * Catches the unchecked {@link NumberFormatException} raised for invalid
     * integer text.
     *
     * <p>
     * The {@code -1} value is only a simple demonstration sentinel and is ambiguous
     * because {@code -1} is also a
     * valid integer. A production parser should use an explicit result type such as
     * {@code OptionalInt} or propagate
     * the parse exception.
     * 
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
     * Reads the first line from a reader that is automatically closed.
     *
     * <p>
     * Try-with-resources calls {@link BufferedReader#close()} on normal return and
     * when {@code readLine()} throws.
     * The {@code IOException} remains checked and is propagated to this method's
     * caller.
     * 
     * @param value text held by the managed reader
     * @return first line from {@code value}, or {@code null} when the input is
     *         empty
     * @throws IOException if reading or closing the reader fails
     */
    public String tryWithResources(String value) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(value))) {
            return reader.readLine();
        }
    }

    /**
     * Wrap an original exception while preserving it as the cause.
     * 
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
