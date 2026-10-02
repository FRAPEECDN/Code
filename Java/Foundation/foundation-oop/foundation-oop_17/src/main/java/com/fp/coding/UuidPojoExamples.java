package com.fp.coding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/** Runnable examples of UUID identity, sorting, cloning, and Java serialization. */
public final class UuidPojoExamples {
    private UuidPojoExamples() {
    }

    /**
     * Runs the UUID POJO examples.
     *
     * @param args command-line arguments; unused
     * @throws IOException if writing or reading the object stream fails
     * @throws ClassNotFoundException if the serialized class cannot be resolved
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        UuidPojoInformation ada = new UuidPojoInformation("Ada Lovelace", 36, 120000.0);
        UuidPojoInformation katherine = new UuidPojoInformation("Katherine Johnson", 36, 95000.0);
        UuidPojoInformation grace = new UuidPojoInformation("Grace Hopper", 85, 150000.0);
        UuidPojoInformation alan = new UuidPojoInformation("Alan Turing", 41, null);
        ArrayList<UuidPojoInformation> people = new ArrayList<>(List.of(ada, grace, alan, katherine));

        System.out.println("Natural Comparable order:");
        people.stream().sorted().forEach(System.out::println);
        System.out.println("Custom Comparator order by age:");
        people.stream().sorted(UuidPojoInformation.BY_AGE).forEach(System.out::println);

        System.out.println("Clone retains UUID: " + ada.equals(ada.clone()));
        System.out.println("New copy gets a different UUID: " + !ada.equals(ada.copyWithNewId()));
        UuidPojoInformation restored = roundTrip(ada);
        System.out.println("Serializable round trip preserves value: " + ada.equals(restored));
    }

    private static UuidPojoInformation roundTrip(UuidPojoInformation value)
            throws IOException, ClassNotFoundException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (UuidPojoInformation) input.readObject();
        }
    }
}