package com.fp.coding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Demonstrates JDK list, set, map, search, sort, and stream APIs with projects. */
public final class JdkCollectionsDemo {
    private JdkCollectionsDemo() {}

    /**
     * Runs the standard-library collection examples.
     *
     * @param projects source projects for the examples
     */
    public static void run(List<Project> projects) {
        demonstrateLists(projects);
        demonstrateSets(projects);
        demonstrateMaps(projects);
        demonstrateSortingAndSearching(projects);
        demonstrateStreams(projects);
    }

    private static void demonstrateLists(List<Project> projects) {
        System.out.println("\nJDK lists and CRUD");

        // ArrayList has fast indexed reads and amortized constant-time append; middle edits shift elements.
        // CopyOnWriteArrayList is concurrent for read-heavy workloads, but each write copies its array.
        List<Project> arrayList = new ArrayList<>(projects);
        Project addition = CollectionDemoSupport.createStandaloneProject("ArrayList Example", 90_000);
        arrayList.add(addition);
        String read = arrayList.get(0).getProjectName();
        arrayList.set(arrayList.size() - 1, projects.get(0));
        arrayList.remove(arrayList.size() - 1);
        System.out.printf("ArrayList CRUD: added=%s, read=%s, size=%d%n",
                addition.getProjectName(), read, arrayList.size());

        // LinkedList supports efficient end operations, but indexed access walks nodes and costs memory.
        // ConcurrentLinkedDeque supports concurrent deque operations, not indexed List operations.
        LinkedList<Project> linkedList = new LinkedList<>(projects);
        System.out.println("LinkedList first/last: " + linkedList.getFirst().getProjectName()
                + " / " + linkedList.getLast().getProjectName());
    }

    private static void demonstrateSets(List<Project> projects) {
        System.out.println("\nJDK sets and CRUD");

        // HashSet has average constant-time membership but no order; keep equals/hashCode stable while stored.
        // ConcurrentHashMap.newKeySet() is the concurrent alternative when encounter order is not needed.
        Set<Project> hashSet = new HashSet<>(projects);
        Project addition = CollectionDemoSupport.createStandaloneProject("HashSet Example", 80_000);
        boolean created = hashSet.add(addition);
        boolean duplicateAdded = hashSet.add(addition);
        boolean read = hashSet.contains(addition);
        hashSet.remove(addition);
        Project replacement = CollectionDemoSupport.createStandaloneProject("HashSet Example Updated", 80_000);
        boolean updated = hashSet.add(replacement);
        boolean deleted = hashSet.remove(replacement);
        System.out.printf("HashSet CRUD: added=%s, duplicate=%s, found=%s, replaced=%s, removed=%s%n",
                created, duplicateAdded, read, updated, deleted);

        // LinkedHashSet preserves encounter order with extra link memory; concurrent hash sets do not preserve it.
        Set<Project> insertionOrdered = new LinkedHashSet<>(projects);
        // TreeSet sorts in O(log n); comparator-equal projects collapse into one entry.
        // ConcurrentSkipListSet is the concurrent sorted-set alternative.
        Set<Project> nameSorted = new TreeSet<>(Comparator.comparing(Project::getProjectName));
        nameSorted.addAll(projects);
        // EnumSet is compact and fast for one enum; use ConcurrentHashMap.newKeySet() for concurrent updates.
        Set<ProjectStatus> statuses = EnumSet.allOf(ProjectStatus.class);
        System.out.println("LinkedHashSet order: " + CollectionDemoSupport.projectNames(insertionOrdered));
        System.out.println("TreeSet by name: " + CollectionDemoSupport.projectNames(nameSorted));
        System.out.println("EnumSet statuses: " + statuses);
    }

    private static void demonstrateMaps(List<Project> projects) {
        System.out.println("\nJDK maps and CRUD");

        // HashMap has average constant-time lookup but no ordering guarantee; ConcurrentHashMap supports concurrent updates.
        Map<String, Project> hashMap = new HashMap<>();
        projects.forEach(project -> hashMap.put(project.getProjectName(), project));
        String key = "HashMap Example";
        Project addition = CollectionDemoSupport.createStandaloneProject(key, 70_000);
        hashMap.put(key, addition);
        String read = hashMap.get(key).getProjectName();
        hashMap.put(key, projects.get(0));
        String updated = hashMap.get(key).getProjectName();
        Project deleted = hashMap.remove(key);
        System.out.printf("HashMap CRUD: added=%s, read=%s, updated=%s, removed=%s%n",
                addition.getProjectName(), read, updated, deleted.getProjectName());

        // LinkedHashMap preserves insertion order with extra link memory; ConcurrentHashMap does not preserve order.
        Map<String, Project> insertionOrdered = new LinkedHashMap<>();
        projects.forEach(project -> insertionOrdered.put(project.getProjectName(), project));
        // TreeMap keeps keys sorted in O(log n); ConcurrentSkipListMap is the concurrent sorted-map alternative.
        Map<String, Project> nameSorted = new TreeMap<>(insertionOrdered);
        // EnumMap is compact and fast for enum keys; use ConcurrentHashMap if concurrent writes are required.
        Map<ProjectStatus, Integer> statusCounts = new EnumMap<>(ProjectStatus.class);
        projects.forEach(project -> statusCounts.merge(project.getStatus(), 1, Integer::sum));
        System.out.println("LinkedHashMap key order: " + insertionOrdered.keySet());
        System.out.println("TreeMap key order: " + nameSorted.keySet());
        System.out.println("EnumMap project counts: " + statusCounts);
    }

    private static void demonstrateSortingAndSearching(List<Project> projects) {
        System.out.println("\nSorting and searching");

        List<Project> byBudget = new ArrayList<>(projects);
        byBudget.sort(Comparator.comparingDouble(Project::getBudget));
        System.out.println("Sorted by budget: " + CollectionDemoSupport.projectNames(byBudget));

        // Linear search is O(n), needs no ordering, and works well for small or unsorted collections.
        Project linearResult = linearSearch(projects, "Cedar");
        List<Project> byName = new ArrayList<>(projects);
        byName.sort(Comparator.comparing(Project::getProjectName));
        Project searchKey = projects.stream()
                .filter(project -> project.getProjectName().equals("Cedar"))
                .findFirst()
                .orElseThrow();
        // Binary search is O(log n) on a random-access list and requires the same comparator used for sorting.
        int index = Collections.binarySearch(byName, searchKey,
                Comparator.comparing(Project::getProjectName));
        String binaryResult = index >= 0 ? byName.get(index).getProjectName() : "not found";
        System.out.println("Linear search for Cedar: " + CollectionDemoSupport.projectName(linearResult));
        System.out.println("Binary search in sorted list: " + binaryResult);
    }

    private static Project linearSearch(List<Project> projects, String name) {
        for (Project project : projects) {
            if (project.getProjectName().equals(name)) {
                return project;
            }
        }
        return null;
    }

    private static void demonstrateStreams(List<Project> projects) {
        System.out.println("\nStreams, predicates, filters, mapping, and grouping");

        // This pipeline is read-only; parallelStream() is appropriate only for large data and thread-safe operations.
        Predicate<Project> planned = project -> project.getStatus() == ProjectStatus.PLANNED;
        Predicate<Project> largeBudget = project -> project.getBudget() >= 300_000;
        List<String> selectedNames = projects.stream()
                .filter(planned.and(largeBudget))
                .map(Project::getProjectName)
                .collect(Collectors.toList());
        double totalBudget = projects.stream().mapToDouble(Project::getBudget).sum();
        double averageBudget = projects.stream().mapToDouble(Project::getBudget).average().orElse(0);
        Map<ProjectStatus, List<Project>> byStatus = projects.stream().collect(
                Collectors.groupingBy(Project::getStatus, () -> new EnumMap<>(ProjectStatus.class),
                        Collectors.toList()));

        System.out.println("Planned projects with budget >= 300000: " + selectedNames);
        System.out.printf("Budget total / average: %.0f / %.0f%n", totalBudget, averageBudget);
        System.out.println("Projects grouped by status: " + CollectionDemoSupport.groupSizes(byStatus));
    }
}
