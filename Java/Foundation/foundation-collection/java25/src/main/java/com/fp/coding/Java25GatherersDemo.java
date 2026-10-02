package com.fp.coding;

import java.util.List;
import java.util.stream.Gatherers;

/** Demonstrates the Stream Gatherers API available to the Java 25 application. */
public final class Java25GatherersDemo {
    private Java25GatherersDemo() {}

    /**
     * Demonstrates fixed windows and a running budget reduction.
     *
     * @param projects ordered sample projects
     */
    public static void run(List<Project> projects) {
        System.out.println("\nStream Gatherers (finalized in Java 24; demonstrated by the Java 25 module)");

        // Gatherers compose into stream pipelines; this demo is sequential and does not mutate its source.
        // Use ConcurrentHashMap for shared concurrent maps and LongAdder for shared concurrent counters.
        List<List<String>> projectWindows = projects.stream()
                .gather(Gatherers.windowFixed(2))
                .map(window -> window.stream().map(Project::getProjectName).toList())
                .toList();
        List<Double> runningBudgets = projects.stream()
                .gather(Gatherers.scan(() -> 0.0,
                        (subtotal, project) -> subtotal + project.getBudget()))
                .toList();

        System.out.println("Fixed-size windows of two projects: " + projectWindows);
        System.out.println("Running budget totals: " + runningBudgets);
    }
}
