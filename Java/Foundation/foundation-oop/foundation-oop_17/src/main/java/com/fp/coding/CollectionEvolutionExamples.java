package com.fp.coding;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Examples of modern collection and Stream APIs available by Java 17. */
public final class CollectionEvolutionExamples {
    private CollectionEvolutionExamples() {
    }

    /** Runs immutable collection factory, {@code Stream.toList()}, and {@code mapMulti()} examples. */
    public static void main(String[] args) {
        List<UuidPojoInformation> immutableList = List.of(
                new UuidPojoInformation("Ada Lovelace", 36, 120000.0),
                new UuidPojoInformation("Grace Hopper", 85, 150000.0));
        Set<String> immutableSet = Set.of("List", "Set", "Map");
        Map<UUID, UuidPojoInformation> immutableMap = Map.of(
                immutableList.get(0).getId(), immutableList.get(0),
                immutableList.get(1).getId(), immutableList.get(1));

        List<String> names = immutableList.stream().map(UuidPojoInformation::getName).toList();
        List<String> nameParts = immutableList.stream()
                .<String>mapMulti((person, downstream) -> {
                    for (String part : person.getName().split(" ")) {
                        downstream.accept(part);
                    }
                })
                .toList();

        System.out.println("Java 9 immutable factories: "
                + immutableList.size() + "/" + immutableSet.size() + "/" + immutableMap.size());
        System.out.println("Java 16 Stream.toList(): " + names);
        System.out.println("Java 16 mapMulti(): " + nameParts);
    }
}