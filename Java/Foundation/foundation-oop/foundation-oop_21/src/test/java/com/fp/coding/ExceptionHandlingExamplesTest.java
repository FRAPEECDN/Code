package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

class ExceptionHandlingExamplesTest {
    @Test
    void distinguishesCheckedAndUncheckedCustomExceptions() throws Exception {
        ExceptionHandlingExamples.Report report = ExceptionHandlingExamples.run();

        assertTrue(report.checkedException().startsWith("MissingNameException:"));
        assertTrue(report.uncheckedException().startsWith("InvalidCapacityException:"));
        assertThrows(ExceptionHandlingExamples.MissingNameException.class,
                () -> ExceptionHandlingExamples.requireName("  "));
        assertThrows(ExceptionHandlingExamples.InvalidCapacityException.class,
                () -> ExceptionHandlingExamples.requirePositiveCapacity(0));
        assertEquals("Ada", ExceptionHandlingExamples.requireName("Ada"));
        assertEquals(2, ExceptionHandlingExamples.requirePositiveCapacity(2));
    }

    @Test
    void tryWithResourcesClosesReaderBeforeReturning() throws Exception {
        Path file = Files.createTempFile("foundation-oop-reader-test-", ".txt");
        try {
            Files.writeString(file, "first line\nsecond line", StandardCharsets.UTF_8);

            assertEquals("first line", ExceptionHandlingExamples.readFirstLine(file));
            assertTrue(Files.deleteIfExists(file));
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
