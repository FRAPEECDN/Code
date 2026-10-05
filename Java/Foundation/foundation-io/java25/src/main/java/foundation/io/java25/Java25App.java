package foundation.io.java25;

import foundation.io.App;

/**
 * Runs the shared examples and the Java 25 structured-concurrency extension.
 */
public final class Java25App {
    private Java25App() {
    }

    /**
     * Runs the baseline demos followed by the preview structured-concurrency
     * example.
     *
     * <p>
     * The second phase starts one file task and one serialization task
     * concurrently, then prints both joined
     * results. The Java 25 Gradle module enables preview features for this entry
     * point.
     *
     * @param args unused command-line arguments
     * @throws Exception if a shared example or a structured subtask fails
     */
    public static void main(String[] args) throws Exception {
        App.main(args);
        StructuredConcurrencyExample.Report result = StructuredConcurrencyExample.run();
        System.out.println("Structured file task: " + result.fileText());
        System.out.println("Structured JSON task: " + result.json());
    }
}
