package com.fp.coding;

import java.util.List;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;

/** Demonstrates mutable list, set, and map APIs from Eclipse Collections. */
public final class EclipseCollectionsDemo {
    private EclipseCollectionsDemo() {}

    /**
     * Runs Eclipse Collections CRUD examples with project values.
     *
     * @param projects source projects
     */
    public static void run(List<Project> projects) {
        System.out.println("\nEclipse Collections");

        // MutableList has a rich API and efficient iteration; it is not thread-safe.
        // CopyOnWriteArrayList is a JDK concurrent-list option when reads greatly outnumber writes.
        MutableList<Project> projectList = Lists.mutable.withAll(projects);
        Project added = CollectionDemoSupport.createStandaloneProject("Eclipse CRUD", 40_000);
        projectList.add(added);
        String listRead = projectList.get(projectList.size() - 1).getProjectName();
        projectList.set(projectList.size() - 1, projects.get(0));
        projectList.remove(projectList.size() - 1);

        // MutableSet provides unique membership; ConcurrentHashMap.newKeySet() is the JDK concurrent alternative.
        MutableSet<Project> projectSet = Sets.mutable.withAll(projects);
        projectSet.add(added);
        boolean setRead = projectSet.contains(added);
        projectSet.remove(added);
        Project setReplacement = CollectionDemoSupport.createStandaloneProject("Eclipse Set Updated", 40_000);
        boolean setUpdated = projectSet.add(setReplacement);
        boolean setDeleted = projectSet.remove(setReplacement);

        // MutableMap has a broad API; ConcurrentHashMap is the JDK alternative for concurrent updates.
        MutableMap<String, Project> projectMap = Maps.mutable.empty();
        projects.forEach(project -> projectMap.put(project.getProjectName(), project));
        projectMap.put(added.getProjectName(), added);
        String mapRead = projectMap.get(added.getProjectName()).getProjectName();
        projectMap.put(added.getProjectName(), projects.get(0));
        projectMap.remove(added.getProjectName());

        System.out.printf("MutableList CRUD: added=%s, read=%s, size=%d%n",
                added.getProjectName(), listRead, projectList.size());
        System.out.printf("MutableSet CRUD: read=%s, replaced=%s, removed=%s%n",
                setRead, setUpdated, setDeleted);
        System.out.printf("MutableMap CRUD: added=%s, read=%s, removed=%s%n",
                added.getProjectName(), mapRead, !projectMap.containsKey(added.getProjectName()));
    }
}
