package com.fp.coding;

import java.util.List;

/** Runs the shared baseline, sequenced collections, and Java 25 Stream Gatherers examples. */
public class App {
    /** Creates the Java 25 example runner. */
    public App() {}

    /**
     * Returns the title printed before the examples.
     * @return the application title
     */
    public String getGreeting() {
        return "Java 25 collection examples";
    }

    /**
     * Runs the shared baseline, sequenced collection, and Gatherers examples.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        List<Project> projects = ProjectCollectionDemo.createSampleProjects();
        System.out.println(new App().getGreeting());
        ProjectCollectionDemo.run(projects, "Java 25");
        SequencedCollectionsDemo.run(projects);
        Java25GatherersDemo.run(projects);
    }
}
