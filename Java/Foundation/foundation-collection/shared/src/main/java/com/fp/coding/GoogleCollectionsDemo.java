package com.fp.coding;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import java.util.List;

/** Demonstrates the Google Guava collection implementations used by this project. */
public final class GoogleCollectionsDemo {
    private GoogleCollectionsDemo() {}

    /**
     * Runs the Guava examples with project values.
     *
     * @param projects source projects
     */
    public static void run(List<Project> projects) {
        System.out.println("\nGoogle Guava");

        // ImmutableList protects list structure, not its mutable Project elements; copying has an allocation cost.
        ImmutableList<Project> snapshot = ImmutableList.copyOf(projects);
        System.out.println("ImmutableList snapshot size: " + snapshot.size());

        // ArrayListMultimap allows repeated values per key and preserves value insertion order, not key order.
        // It is not thread-safe; Multimaps.synchronizedListMultimap serializes access when that is sufficient.
        // Callers must still synchronize on the wrapper while iterating its views.
        ListMultimap<ProjectStatus, Project> projectsByStatus = ArrayListMultimap.create();
        projects.forEach(project -> projectsByStatus.put(project.getStatus(), project));

        Project added = CollectionDemoSupport.createStandaloneProject("Guava CRUD", 55_000);
        projectsByStatus.put(added.getStatus(), added);
        String read = projectsByStatus.get(ProjectStatus.REGISTERED).stream()
                .filter(project -> project.getProjectName().equals("Guava CRUD"))
                .findFirst().orElseThrow().getProjectName();

        projectsByStatus.remove(ProjectStatus.REGISTERED, added);
        added.transitionTo(ProjectStatus.PLANNED, CollectionDemoSupport.REGISTERED_ON.plusDays(1));
        projectsByStatus.put(added.getStatus(), added);
        boolean updated = projectsByStatus.get(ProjectStatus.PLANNED).contains(added);
        boolean deleted = projectsByStatus.remove(ProjectStatus.PLANNED, added);
        System.out.printf("ListMultimap CRUD: added=%s, read=%s, moved=%s, removed=%s%n",
                added.getProjectName(), read, updated, deleted);
    }
}
