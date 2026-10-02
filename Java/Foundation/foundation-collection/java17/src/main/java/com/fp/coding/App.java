package com.fp.coding;

/** Entry point for the Java 17 collections baseline. */
public class App {
    /** Creates the Java 17 example runner. */
    public App() {}

    /**
     * Returns the title printed before the baseline examples.
     * @return the application title
     */
    public String getGreeting() {
    return "Java 17 collection examples";
    }

    /**
     * Runs the Java 17 collection examples.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
    System.out.println(new App().getGreeting());
    ProjectCollectionDemo.run();
    }
}
