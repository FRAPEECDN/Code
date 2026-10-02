package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class CollectionExamplesTest {
    @Test
    void linearAndBinarySearchFindNamesAndReportMissingValues() {
        var people = CollectionExamples.samplePeople();
        assertEquals(1, CollectionExamples.linearSearchByName(people, "grace hopper"));
        assertEquals(-1, CollectionExamples.linearSearchByName(people, "missing"));

        var sorted = new ArrayList<>(people);
        sorted.sort(CollectionExamples.BY_NAME);
        assertTrue(CollectionExamples.binarySearchByName(sorted, "GRACE HOPPER") >= 0);
        assertTrue(CollectionExamples.binarySearchByName(sorted, "missing") < 0);
    }

    @Test
    void allThreeCollectionAndStreamSectionsRun() throws Exception {
        String output = captureOutput(() -> CollectionExamples.main(new String[0]));
        assertTrue(output.contains("List API operations"));
        assertTrue(output.contains("Set API operations"));
        assertTrue(output.contains("Map API operations"));
        assertTrue(output.contains("Stream operations over List"));
        assertTrue(output.contains("Stream operations over Set"));
        assertTrue(output.contains("Stream operations over Map values"));
        assertTrue(output.contains("Binary search for Grace Hopper"));
        assertTrue(output.contains("partitioning/toMap"));
    }

    @Test
    void java17CollectionEvolutionExamplesRun() throws Exception {
        String output = captureOutput(() -> CollectionEvolutionExamples.main(new String[0]));
        assertTrue(output.contains("Java 9 immutable factories"));
        assertTrue(output.contains("Stream.toList()"));
        assertTrue(output.contains("mapMulti()"));
    }

    private static String captureOutput(ThrowingRunnable action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}