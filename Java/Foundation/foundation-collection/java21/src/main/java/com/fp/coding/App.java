package com.fp.coding;

import java.util.List;

/** Runs the shared collection baseline and Java 21 sequenced-collection examples. */
public class App {
    /** Creates the Java 21 example runner. */
    public App() {}

    /**
     * Returns the title printed before the examples.
     * @return the application title
     */
    public String getGreeting() {
        return "Java 21 collection examples";
    }

    /**
     * Runs the shared baseline and Java 21 sequenced-collection examples.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        List<Project> projects = ProjectCollectionDemo.createSampleProjects();
        System.out.println(new App().getGreeting());
        ProjectCollectionDemo.run(projects, "Java 21");
        SequencedCollectionsDemo.run(projects);
    }
}
