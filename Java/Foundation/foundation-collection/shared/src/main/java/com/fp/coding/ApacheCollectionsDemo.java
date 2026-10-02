package com.fp.coding;

import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

/** Demonstrates Apache Commons Collections filtering and multi-valued maps. */
public final class ApacheCollectionsDemo {
    private ApacheCollectionsDemo() {}

    /**
     * Runs Apache Commons Collections examples with project values.
     *
     * @param projects source projects
     */
    public static void run(List<Project> projects) {
        System.out.println("\nApache Commons Collections");

        // select() traverses the input and allocates a result collection; use a stream for a lazy pipeline.
        Collection<Project> selected = CollectionUtils.select(
                projects, project -> project.getBudget() >= 300_000);
        System.out.println("CollectionUtils.select budget >= 300000: "
                + CollectionDemoSupport.projectNames(selected));

        // ArrayListValuedHashMap allows duplicate values per key, but is mutable and not thread-safe.
        // ConcurrentHashMap<K, CopyOnWriteArrayList<V>> is an option for concurrent, read-heavy values.
        MultiValuedMap<ProjectStatus, Project> projectsByStatus = new ArrayListValuedHashMap<>();
        projects.forEach(project -> projectsByStatus.put(project.getStatus(), project));

        Project added = CollectionDemoSupport.createStandaloneProject("Apache CRUD", 50_000);
        projectsByStatus.put(added.getStatus(), added);
        String read = projectsByStatus.get(ProjectStatus.REGISTERED).stream()
                .filter(project -> project.getProjectName().equals("Apache CRUD"))
                .findFirst().orElseThrow().getProjectName();

        projectsByStatus.removeMapping(ProjectStatus.REGISTERED, added);
        added.transitionTo(ProjectStatus.PLANNED, CollectionDemoSupport.REGISTERED_ON.plusDays(1));
        projectsByStatus.put(added.getStatus(), added);
        boolean updated = projectsByStatus.get(ProjectStatus.PLANNED).contains(added);
        boolean deleted = projectsByStatus.removeMapping(ProjectStatus.PLANNED, added);
        System.out.printf("MultiValuedMap CRUD: added=%s, read=%s, moved=%s, removed=%s%n",
                added.getProjectName(), read, updated, deleted);
    }
}
