package com.fp.coding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;

/** Demonstrates the sequenced collection interfaces introduced in Java 21. */
public final class SequencedCollectionsDemo {
    private SequencedCollectionsDemo() {}

    /**
     * Demonstrates first/last access, reverse views, and end-position mutations.
     *
     * @param projects ordered sample projects
     */
    public static void run(List<Project> projects) {
        System.out.println("\nJava 21 sequenced collections");

        // Sequenced interfaces preserve encounter order but are not thread-safe; use ConcurrentLinkedDeque
        // for a concurrent deque, accepting that it does not provide List indexing or arbitrary reordering.
        SequencedCollection<Project> sequence = new ArrayList<>(projects);
        System.out.println("SequencedCollection first/last: "
                + sequence.getFirst().getProjectName() + " / " + sequence.getLast().getProjectName());
        System.out.println("Reversed view: " + sequence.reversed().stream()
                .map(Project::getProjectName).toList());
        sequence.addFirst(projects.get(3));
        Project promoted = sequence.removeFirst();
        System.out.println("Add/remove at front: " + promoted.getProjectName());

        // LinkedHashSet can reposition elements at either end; ConcurrentHashMap.newKeySet() is concurrent
        // when encounter-order guarantees are not required.
        SequencedSet<Project> orderedProjects = new LinkedHashSet<>(projects);
        orderedProjects.addFirst(projects.get(3));
        System.out.println("SequencedSet after moving Delta to front: "
                + orderedProjects.stream().map(Project::getProjectName).toList());

        // LinkedHashMap retains insertion order; ConcurrentHashMap is concurrent but does not retain that order.
        SequencedMap<String, Project> projectMap = new LinkedHashMap<>();
        projects.forEach(project -> projectMap.put(project.getProjectName(), project));
        projectMap.putFirst("Featured", projects.get(0));
        projectMap.putLast("Recently viewed", projects.get(1));
        Map.Entry<String, Project> first = projectMap.firstEntry();
        Map.Entry<String, Project> last = projectMap.lastEntry();
        System.out.println("SequencedMap first/last: " + first.getKey() + " / " + last.getKey());
        System.out.println("Reversed map keys: " + projectMap.reversed().keySet());
        System.out.println("Remove last entry: " + projectMap.pollLastEntry().getKey());
    }
}
