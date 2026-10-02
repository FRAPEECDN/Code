package com.fp.coding;

import java.util.Objects;
import java.util.Optional;

/**
 * Runnable examples of {@link Objects} helpers and {@link Optional} with POJOs and records.
 *
 * <p>The catalog demonstrates a has-a relationship and returns optional lookup
 * results. The main method shows null-safe equality, hashing, string conversion,
 * mapping an optional bean to its nullable salary, and handling absent values.
 */
public final class ObjectsOptionalExamples {
    private ObjectsOptionalExamples() {
    }

    /** Runs the Objects and Optional examples and prints their results.
     *
     * @param args command-line arguments; unused
     */
    public static void main(String[] args) {
        PojoInformation pojo = new PojoInformation(101, "Ada Lovelace", 36, 120000.0);
        PojoInformation pojoWithoutSalary = new PojoInformation(102, "Katherine Johnson", 26);
        RecordInformation record = new RecordInformation("Grace Hopper", 85, 150000.0);

        InformationCatalog catalog = new InformationCatalog();
        catalog.add(pojo);
        catalog.add(pojoWithoutSalary);
        catalog.add(record);

        System.out.println("Objects.equals on a POJO copy: " + Objects.equals(pojo, pojo.copy()));
        System.out.println("Objects.equals on equal records: "
                + Objects.equals(record, new RecordInformation("Grace Hopper", 85, 150000.0)));
        System.out.println("Objects.hash for record components: "
                + Objects.hash(record.name(), record.age(), record.salary()));

        Optional<PojoInformation> foundPojo = catalog.findPojoById(101);
        System.out.println("Found POJO: " + foundPojo.map(Objects::toString).orElse("No POJO found"));

        Optional<RecordInformation> foundRecord = catalog.findRecordByName("Grace Hopper");
        foundRecord.map(RecordInformation::summary).ifPresentOrElse(
                summary -> System.out.println("Found record: " + summary),
                () -> System.out.println("No record found"));
        catalog.findRecordByName("Unknown").map(RecordInformation::summary).ifPresentOrElse(
                summary -> System.out.println("Found record: " + summary),
                () -> System.out.println("Optional lookup returned no record"));

        System.out.println("Missing record: " + catalog.findRecordByName("Unknown")
                .map(RecordInformation::summary)
                .orElse("No matching record"));
        System.out.println("Present POJO salary: " + catalog.findPojoSalary(101)
                .map(Object::toString).orElse("No salary available"));
        System.out.println("Null POJO salary: " + catalog.findPojoSalary(102)
                .map(Object::toString).orElse("No salary available"));
        System.out.println("Missing POJO: " + catalog.findPojoSalary(999)
                .map(Object::toString).orElse("No matching POJO"));

        catalog.entries().forEach(entry -> System.out.println("Polymorphic summary: " + entry.summary()));
    }
}