package com.fp.coding;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Runnable examples of checked exceptions, unchecked exceptions, and
 * try-with-resources.
 *
 * <p>
 * A checked exception is part of a method's compile-time contract: callers must
 * catch it or declare it. An
 * unchecked exception extends {@link RuntimeException}; callers may catch it,
 * but the compiler does not require it.
 * Try-with-resources closes an {@link AutoCloseable} resource at the end of its
 * block, including when reading fails.
 */
public final class ExceptionHandlingExamples {
    private ExceptionHandlingExamples() {
    }

    /**
     * Runs the checked, unchecked, and resource-management examples and returns
     * their results for display.
     *
     * @return the caught exception descriptions and text read from the managed
     *         resource
     * @throws IOException if the temporary demonstration file cannot be written,
     *                     read, or deleted
     */
    public static Report run() throws IOException {
        String checkedDescription;
        try {
            requireName("   ");
            checkedDescription = "No checked exception was thrown";
        } catch (MissingNameException exception) {
            checkedDescription = exception.getClass().getSimpleName() + ": " + exception.getMessage();
        }

        String uncheckedDescription;
        try {
            requirePositiveCapacity(0);
            uncheckedDescription = "No unchecked exception was thrown";
        } catch (InvalidCapacityException exception) {
            uncheckedDescription = exception.getClass().getSimpleName() + ": " + exception.getMessage();
        }

        Path file = Files.createTempFile("foundation-oop-exception-", ".txt");
        try {
            Files.writeString(file, "The reader closes automatically.", StandardCharsets.UTF_8);
            String firstLine = readFirstLine(file);
            return new Report(checkedDescription, uncheckedDescription, firstLine);
        } finally {
            Files.deleteIfExists(file);
        }
    }

    /**
     * Rejects a missing name using a checked exception.
     *
     * @param name candidate name
     * @return the nonblank name, unchanged
     * @throws MissingNameException if the name is null or blank; callers must catch
     *                              or declare this exception
     */
    public static String requireName(String name) throws MissingNameException {
        if (name == null || name.isBlank()) {
            throw new MissingNameException("name must contain text");
        }
        return name;
    }

    /**
     * Validates a capacity using an unchecked exception.
     *
     * @param capacity requested capacity
     * @return the positive capacity
     * @throws InvalidCapacityException if the capacity is zero or negative; callers
     *                                  are not required to catch it
     */
    public static int requirePositiveCapacity(int capacity) {
        if (capacity <= 0) {
            throw new InvalidCapacityException("capacity must be positive");
        }
        return capacity;
    }

    /**
     * Reads the first UTF-8 line and closes the reader automatically.
     *
     * <p>
     * The try-with-resources declaration guarantees {@link BufferedReader#close()}
     * runs both after a successful
     * read and while an {@link IOException} is propagating. The caller still has to
     * handle or declare that checked
     * I/O exception.
     *
     * @param path file to read
     * @return first line, or {@code null} when the file is empty
     * @throws IOException if opening, reading, or closing the file fails
     */
    public static String readFirstLine(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return reader.readLine();
        }
    }

    /** Runs the examples and prints the result of each exception/resource case. */
    public static void main(String[] args) throws IOException {
        Report report = run();
        System.out.println("Checked exception (caught): " + report.checkedException());
        System.out.println("Unchecked exception (caught): " + report.uncheckedException());
        System.out.println("Try-with-resources read: " + report.resourceText());
    }

    /**
     * Result values from the exception demonstration.
     *
     * @param checkedException   description of the caught checked exception
     * @param uncheckedException description of the caught unchecked exception
     * @param resourceText       text read before the resource was closed
     */
    public record Report(String checkedException, String uncheckedException, String resourceText) {
    }

    /** Checked exception for invalid required names. */
    public static final class MissingNameException extends Exception {
        private static final long serialVersionUID = 1L;

        /** Creates the checked name-validation exception. */
        public MissingNameException(String message) {
            super(message);
        }
    }

    /** Unchecked exception for an invalid capacity argument. */
    public static final class InvalidCapacityException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        /** Creates the unchecked capacity-validation exception. */
        public InvalidCapacityException(String message) {
            super(message);
        }
    }
}
