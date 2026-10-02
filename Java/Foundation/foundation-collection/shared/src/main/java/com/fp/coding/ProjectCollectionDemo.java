package com.fp.coding;

import java.time.LocalDate;
import java.util.List;

/** Coordinates focused JDK and third-party collection demonstrations over a shared project dataset. */
public final class ProjectCollectionDemo {
    private ProjectCollectionDemo() {}

    /** Runs the shared demonstrations using the Java 17 baseline label. */
    public static void run() {
        run(createSampleProjects(), "Java 17");
    }

    /**
     * Creates a new owner and a fresh sample project dataset.
     *
     * @return an immutable snapshot of registered sample projects
     */
    public static List<Project> createSampleProjects() {
        return createProjects(new ProjectOwner());
    }

    /**
     * Runs each collection example in its own focused class.
     *
     * @param projects sample projects to use for each example
     * @param versionLabel Java version label used by the tuple example
     */
    public static void run(List<Project> projects, String versionLabel) {
        System.out.println("\nJava collections baseline");
        JdkCollectionsDemo.run(projects);
        GoogleCollectionsDemo.run(projects);
        ApacheCollectionsDemo.run(projects);
        EclipseCollectionsDemo.run(projects);
        JavaTuplesDemo.run(projects.get(0), versionLabel);
    }

    private static List<Project> createProjects(ProjectOwner owner) {
        LocalDate registeredOn = CollectionDemoSupport.REGISTERED_ON;
        Project atlas = createProject(owner, "Atlas", 480_000, registeredOn);
        Project beacon = createProject(owner, "Beacon", 325_000, registeredOn);
        Project cedar = createProject(owner, "Cedar", 220_000, registeredOn);
        Project delta = createProject(owner, "Delta", 610_000, registeredOn);

        beacon.transitionTo(ProjectStatus.PLANNED, registeredOn.plusDays(1));
        cedar.transitionTo(ProjectStatus.PLANNED, registeredOn.plusDays(1));
        cedar.transitionTo(ProjectStatus.IMPLEMENTATING, registeredOn.plusDays(2));
        delta.transitionTo(ProjectStatus.PLANNED, registeredOn.plusDays(1));
        delta.transitionTo(ProjectStatus.IMPLEMENTATING, registeredOn.plusDays(2));
        delta.transitionTo(ProjectStatus.FINISHED, registeredOn.plusDays(3));

        return owner.getProjects();
    }

    private static Project createProject(
            ProjectOwner owner, String name, double budget, LocalDate registeredOn) {
        LocalDate startDate = registeredOn.plusDays(1);
        return Project.builder()
                .projectOwner(owner)
                .projectName(name)
                .sponsor("Foundation")
                .budget(budget)
                .startDate(startDate)
                .deadline(startDate.plusMonths(6))
                .registeredOn(registeredOn)
                .build();
    }
}
