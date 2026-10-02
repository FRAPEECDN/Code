package com.fp.coding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.UUID;

/** Runnable examples of Java 21 sequenced collection interfaces and operations. */
public final class CollectionEvolutionExamples {
    private CollectionEvolutionExamples() {
    }

    /** Runs Java 21 first/last and reverse-view collection examples. */
    public static void main(String[] args) {
        List<UuidPojoInformation> people = new ArrayList<>(CollectionExamples.samplePeople());
        System.out.println("Sequenced List first/last: "
                + people.getFirst().getName() + "/" + people.getLast().getName());
        System.out.println("Sequenced List reversed view: "
                + people.reversed().stream().map(UuidPojoInformation::getName).toList());

        SequencedSet<UuidPojoInformation> peopleSet = new LinkedHashSet<>(people);
        UuidPojoInformation first = people.getFirst();
        peopleSet.remove(first);
        peopleSet.addFirst(first);
        System.out.println("Sequenced Set first/last: "
                + peopleSet.getFirst().getName() + "/" + peopleSet.getLast().getName());

        SequencedMap<UUID, UuidPojoInformation> peopleById = new LinkedHashMap<>();
        people.forEach(person -> peopleById.putLast(person.getId(), person));
        System.out.println("Sequenced Map first/last entry: "
                + peopleById.firstEntry().getValue().getName() + "/"
                + peopleById.lastEntry().getValue().getName());
        System.out.println("Sequenced Map reversed keys: " + peopleById.reversed().keySet());
    }
}