package com.fp.coding;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.UUID;
import java.util.stream.Gatherers;

/** Runnable examples of sequenced collections and Java 25 stream Gatherers. */
public final class CollectionEvolutionExamples {
    private CollectionEvolutionExamples() {
    }

    /** Runs Java 21 sequenced collections and Java 24+ Gatherer stream operations. */
    public static void main(String[] args) {
        List<UuidPojoInformation> people = new ArrayList<>(CollectionExamples.samplePeople());
        System.out.println("Sequenced List reversed: "
                + people.reversed().stream().map(UuidPojoInformation::getName).toList());

        SequencedSet<UuidPojoInformation> peopleSet = new LinkedHashSet<>(people);
        System.out.println("Sequenced Set first/last: "
                + peopleSet.getFirst().getName() + "/" + peopleSet.getLast().getName());

        SequencedMap<UUID, UuidPojoInformation> peopleById = new LinkedHashMap<>();
        people.forEach(person -> peopleById.putLast(person.getId(), person));
        System.out.println("Sequenced Map first/last: "
                + peopleById.firstEntry().getValue().getName() + "/"
                + peopleById.lastEntry().getValue().getName());

        List<List<String>> fixedWindows = people.stream()
                .gather(Gatherers.windowFixed(2))
                .map(window -> window.stream().map(UuidPojoInformation::getName).toList())
                .toList();
        List<List<String>> slidingWindows = people.stream()
                .gather(Gatherers.windowSliding(2))
                .map(window -> window.stream().map(UuidPojoInformation::getName).toList())
                .toList();
        System.out.println("Gatherers.windowFixed(2): " + fixedWindows);
        System.out.println("Gatherers.windowSliding(2): " + slidingWindows);
    }
}