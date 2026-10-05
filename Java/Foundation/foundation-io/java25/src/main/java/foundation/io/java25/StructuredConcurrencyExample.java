package foundation.io.java25;

import java.util.concurrent.StructuredTaskScope;

import foundation.io.files.FileIoExamples;
import foundation.io.serialization.SerializationExamples;

/**
 * Demonstrates Java 25 preview structured concurrency by joining independent
 * I/O tasks.
 *
 * <p>
 * {@link StructuredTaskScope} gives the two subtasks one lexical owner and
 * lifetime. The scope starts both
 * operations on virtual threads, waits for successful completion, and makes
 * both results available before it closes.
 * If either subtask fails, {@code awaitAllSuccessfulOrThrow()} cancels the
 * scope and reports the failure instead of
 * allowing an unobserved background task to outlive the operation.
 *
 * <p>
 * This example is intentionally version-specific because
 * {@link StructuredTaskScope} is a preview API in Java 25.
 * Its use is confined to the Java 25 source set and preview flags.
 */
public final class StructuredConcurrencyExample {
    private StructuredConcurrencyExample() {
    }

    /**
     * Reads a file and serializes a message concurrently in one structured task
     * scope.
     *
     * <p>
     * Each {@code fork} starts an independent subtask. {@code join} waits for both
     * tasks and applies the configured
     * all-success policy; only after joining does the owner thread call {@code get}
     * on each subtask. Closing the
     * try-with-resources scope ensures no child task can continue beyond this
     * method's scope.
     *
     * @return the NIO.2 text and JSON produced by the two subtasks
     * @throws InterruptedException                if the owner thread is
     *                                             interrupted while joining
     *                                             subtasks
     * @throws StructuredTaskScope.FailedException if either subtask fails
     */
    public static Report run() throws InterruptedException {
        try (var scope = StructuredTaskScope.<String, Void>open(
                StructuredTaskScope.Joiner.<String>awaitAllSuccessfulOrThrow())) {
            var fileTask = scope.fork(() -> FileIoExamples.run().nioText());
            var jsonTask = scope.fork(() -> SerializationExamples.run().json());
            scope.join();
            return new Report(fileTask.get(), jsonTask.get());
        }
    }

    /**
     * Results produced by the structured subtasks.
     *
     * @param fileText text returned by the file-reading task
     * @param json     JSON returned by the serialization task
     */
    public record Report(String fileText, String json) {
    }
}
