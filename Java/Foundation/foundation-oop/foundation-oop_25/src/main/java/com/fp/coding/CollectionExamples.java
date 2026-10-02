package com.fp.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Runnable tour of List, Set, Map, sorting, searching, and streams with UUID POJOs.
 *
 * <p>Hash-based collections contain beans that are not mutated while stored because
 * this example bean's hash code includes mutable value fields.
 */
public final class CollectionExamples {
    /** Case-insensitive name ordering used by the list sort and binary search examples. */
    public static final Comparator<UuidPojoInformation> BY_NAME =
            Comparator.comparing(UuidPojoInformation::getName, String.CASE_INSENSITIVE_ORDER);

    private CollectionExamples() {
    }

    /** Runs the List, Set, Map, search, and Stream examples. */
    public static void main(String[] args) {
        List<UuidPojoInformation> people = samplePeople();
        demonstrateList(people);
        demonstrateSet(people);
        Map<UUID, UuidPojoInformation> peopleById = demonstrateMap(people);

        System.out.println("Linear search for Grace Hopper: "
                + linearSearchByName(people, "Grace Hopper"));
        ArrayList<UuidPojoInformation> sortedByName = new ArrayList<>(people);
        sortedByName.sort(BY_NAME);
        System.out.println("Binary search for Grace Hopper: "
                + binarySearchByName(sortedByName, "Grace Hopper"));
        System.out.println("Binary search for missing name: "
                + binarySearchByName(sortedByName, "No Such Person"));

        demonstrateStreams("List", people);
        demonstrateStreams("Set", new LinkedHashSet<>(people));
        demonstrateStreams("Map values", peopleById.values());
    }

    static List<UuidPojoInformation> samplePeople() {
        return List.of(
                new UuidPojoInformation("Ada Lovelace", 36, 120000.0),
                new UuidPojoInformation("Grace Hopper", 85, 150000.0),
                new UuidPojoInformation("Alan Turing", 41, null),
                new UuidPojoInformation("Katherine Johnson", 36, 95000.0));
    }

    /** Performs a case-insensitive sequential lookup. */
    public static int linearSearchByName(List<UuidPojoInformation> people, String name) {
        for (int index = 0; index < people.size(); index++) {
            if (people.get(index).getName().equalsIgnoreCase(name)) {
                return index;
            }
        }
        return -1;
    }

    /** Performs a case-insensitive binary lookup in a list already sorted by {@link #BY_NAME}. */
    public static int binarySearchByName(List<UuidPojoInformation> sortedPeople, String name) {
        UuidPojoInformation key = new UuidPojoInformation(new UUID(0, 0), name, 1, null);
        return Collections.binarySearch(sortedPeople, key, BY_NAME);
    }

    private static void demonstrateList(List<UuidPojoInformation> people) {
        System.out.println("\nList API operations:");
        ArrayList<UuidPojoInformation> working = new ArrayList<>();
        working.add(people.get(0));
        working.addAll(people.subList(1, people.size()));
        working.add(1, people.get(0));
        System.out.println("size/isEmpty: " + working.size() + "/" + working.isEmpty());
        System.out.println("get/indexOf/lastIndexOf: " + working.get(0).getName() + "/"
                + working.indexOf(people.get(0)) + "/" + working.lastIndexOf(people.get(0)));
        System.out.println("contains/containsAll: "
                + working.contains(people.get(0)) + "/" + working.containsAll(people));
        working.set(1, people.get(1));
        working.remove(0);
        working.remove(people.get(2));
        working.removeAll(List.of(people.get(3)));
        working.retainAll(List.of(people.get(1)));
        working.addAll(people);
        working.removeIf(person -> person.getAge() < 30);
        working.replaceAll(UuidPojoInformation::clone);
        working.sort(BY_NAME);
        System.out.println("sorted/subList: " + working.stream().map(UuidPojoInformation::getName).toList()
                + "/" + working.subList(0, Math.min(2, working.size())).size());
        System.out.println("toArray length: " + working.toArray(UuidPojoInformation[]::new).length);
        System.out.println("spliterator characteristics: " + working.spliterator().characteristics());
        working.forEach(person -> System.out.print(person.getName() + " "));
        System.out.println();
        working.clear();
        System.out.println("clear/isEmpty: " + working.isEmpty());
    }

    private static void demonstrateSet(List<UuidPojoInformation> people) {
        System.out.println("\nSet API operations:");
        LinkedHashSet<UuidPojoInformation> peopleSet = new LinkedHashSet<>();
        people.forEach(peopleSet::add);
        System.out.println("add equal clone: " + peopleSet.add(people.get(0).clone()));
        System.out.println("addAll/size: " + peopleSet.addAll(people) + "/" + peopleSet.size());
        System.out.println("contains/containsAll: "
                + peopleSet.contains(people.get(0)) + "/" + peopleSet.containsAll(people));
        System.out.println("toArray length: " + peopleSet.toArray(UuidPojoInformation[]::new).length);
        peopleSet.iterator().forEachRemaining(person -> System.out.print(person.getName() + " "));
        System.out.println();

        Set<UuidPojoInformation> working = new LinkedHashSet<>(peopleSet);
        working.remove(people.get(0));
        working.removeAll(Set.of(people.get(1)));
        working.retainAll(Set.of(people.get(2), people.get(3)));
        working.addAll(people);
        working.removeIf(person -> person.getAge() < 40);
        System.out.println("remove/removeAll/retainAll/removeIf size: " + working.size());
        working.clear();
        System.out.println("clear/isEmpty: " + working.isEmpty());
    }

    private static Map<UUID, UuidPojoInformation> demonstrateMap(List<UuidPojoInformation> people) {
        System.out.println("\nMap API operations:");
        LinkedHashMap<UUID, UuidPojoInformation> peopleById = new LinkedHashMap<>();
        Map<UUID, UuidPojoInformation> initialEntries = new HashMap<>();
        people.forEach(person -> initialEntries.put(person.getId(), person));
        peopleById.putAll(initialEntries);
        UuidPojoInformation first = people.get(0);
        peopleById.putIfAbsent(first.getId(), first);
        System.out.println("size/isEmpty: " + peopleById.size() + "/" + peopleById.isEmpty());
        System.out.println("get/getOrDefault: " + peopleById.get(first.getId()).getName() + "/"
                + peopleById.getOrDefault(new UUID(0, 1), first).getName());
        System.out.println("containsKey/containsValue: "
                + peopleById.containsKey(first.getId()) + "/" + peopleById.containsValue(first));
        peopleById.replace(first.getId(), first.clone());
        peopleById.compute(first.getId(), (id, person) -> person);
        peopleById.computeIfPresent(first.getId(), (id, person) -> person.clone());
        UUID addedId = UUID.randomUUID();
        peopleById.computeIfAbsent(addedId,
                id -> new UuidPojoInformation(id, "Dorothy Vaughan", 32, 90000.0));
        peopleById.merge(first.getId(), first,
                (existing, incoming) -> existing.getAge() >= incoming.getAge() ? existing : incoming);
        peopleById.replaceAll((id, person) -> person.clone());
        System.out.println("keySet/values/entrySet: " + peopleById.keySet().size() + "/"
                + peopleById.values().size() + "/" + peopleById.entrySet().size());
        peopleById.forEach((id, person) -> System.out.println(id + " -> " + person.getName()));
        peopleById.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.print(entry.getValue().getName() + " "));
        System.out.println();

        LinkedHashMap<UUID, UuidPojoInformation> working = new LinkedHashMap<>(peopleById);
        working.remove(addedId);
        working.remove(first.getId(), first.clone());
        working.clear();
        System.out.println("remove/clear/isEmpty: " + working.size() + "/" + working.isEmpty());
        return peopleById;
    }

    private static void demonstrateStreams(String sourceName, Collection<UuidPojoInformation> people) {
        System.out.println("\nStream operations over " + sourceName + ":");
        List<String> names = people.stream()
                .filter(person -> person.getAge() >= 35)
                .map(UuidPojoInformation::getName)
                .distinct()
                .sorted()
                .toList();
        System.out.println("filter/map/distinct/sorted/toList: " + names);
        List<String> nameParts = people.stream()
                .flatMap(person -> Arrays.stream(person.getName().split(" ")))
                .distinct().sorted().toList();
        System.out.println("flatMap: " + nameParts);
        List<String> mappedMulti = people.stream()
                .<String>mapMulti((person, downstream) -> downstream.accept(person.getName())).toList();
        System.out.println("mapMulti: " + mappedMulti);
        System.out.println("skip/limit: " + people.stream().skip(1).limit(2)
                .map(UuidPojoInformation::getName).toList());

        List<UuidPojoInformation> ageSorted = people.stream()
                .sorted(Comparator.comparingInt(UuidPojoInformation::getAge)).toList();
        System.out.println("takeWhile/dropWhile: "
                + ageSorted.stream().takeWhile(person -> person.getAge() < 40).count() + "/"
                + ageSorted.stream().dropWhile(person -> person.getAge() < 40).count());
        System.out.println("peek/forEach:");
        people.stream().peek(person -> System.out.println("visiting " + person.getName()))
                .forEach(person -> { });

        System.out.println("count/min/max: " + people.stream().count() + "/"
                + people.stream().min(Comparator.naturalOrder()).map(UuidPojoInformation::getName).orElse("none")
                + "/" + people.stream().max(Comparator.naturalOrder()).map(UuidPojoInformation::getName).orElse("none"));
        System.out.println("matches: " + people.stream().anyMatch(person -> person.getAge() > 80) + "/"
                + people.stream().allMatch(person -> person.getAge() > 0) + "/"
                + people.stream().noneMatch(person -> person.getAge() < 0));
        System.out.println("findFirst/findAny: " + people.stream().findFirst().map(UuidPojoInformation::getName)
                + "/" + people.stream().findAny().map(UuidPojoInformation::getName));
        System.out.println("age summary statistics: " + people.stream()
                .mapToInt(UuidPojoInformation::getAge).summaryStatistics());
        System.out.println("average age: " + people.stream().mapToInt(UuidPojoInformation::getAge).average());
        System.out.println("salary reduce: " + people.stream().map(UuidPojoInformation::getSalary)
                .filter(salary -> salary != null).reduce(0.0, Double::sum));
        System.out.println("parallel salary sum: " + people.parallelStream()
                .map(UuidPojoInformation::getSalary).filter(salary -> salary != null)
                .mapToDouble(Double::doubleValue).sum());

        Map<Integer, Long> ageGroups = people.stream().collect(Collectors.groupingBy(
                person -> person.getAge() / 10 * 10, Collectors.counting()));
        Map<Boolean, List<String>> agePartitions = people.stream().collect(Collectors.partitioningBy(
                person -> person.getAge() >= 40,
                Collectors.mapping(UuidPojoInformation::getName, Collectors.toList())));
        Map<UUID, String> namesById = people.stream().collect(Collectors.toMap(
                UuidPojoInformation::getId, UuidPojoInformation::getName));
        String joinedNames = people.stream().map(UuidPojoInformation::getName)
                .collect(Collectors.joining(", "));
        Set<String> namesAsSet = people.stream().map(UuidPojoInformation::getName)
                .collect(Collectors.toCollection(HashSet::new));
        System.out.println("grouping/partitioning/toMap: " + ageGroups + "/" + agePartitions
                + "/" + namesById.size());
        System.out.println("joining/toCollection: " + joinedNames + "/" + namesAsSet.size());
        System.out.println("toArray/parallel count: "
                + people.stream().toArray(UuidPojoInformation[]::new).length + "/" + people.parallelStream().count());
    }
}