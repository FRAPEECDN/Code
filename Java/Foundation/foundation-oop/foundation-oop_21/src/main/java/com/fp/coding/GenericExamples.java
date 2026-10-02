package com.fp.coding;

import java.util.ArrayList;
import java.util.List;

/**
 * Runnable Java 21 tour of the project's enum, inheritance, record, and generics examples.
 *
 * <p>The demo combines separately typed {@link ArrayList} instances in a common
 * {@code Information} collection, then exercises record-pattern switches and
 * Java 21 sequenced-collection operations such as {@code getFirst()} and {@code getLast()}.
 */
public final class GenericExamples {
    private GenericExamples() {
    }

        /** Runs the Java 21 examples and prints their results to standard output.
         *
         * @param args command-line arguments; unused
         */
        public static void main(String[] args) {
        ArrayList<PojoInformation> pojos = new ArrayList<>(List.of(
                new PojoInformation(1, "Ada Lovelace", 36, 120000.0)));
        ArrayList<RecordInformation> records = new ArrayList<>(List.of(
                new RecordInformation("Grace Hopper", 85, 150000.0)));
        ArrayList<Manager> managers = new ArrayList<>(List.of(
                new Manager("Katherine Johnson", 145000.0, 8)));
        ArrayList<Developer> developers = new ArrayList<>(List.of(
                new Developer("Alan Turing", 110000.0, "Algorithms")));
        ArrayList<UuidPojoInformation> uuidPojos = new ArrayList<>(List.of(
                new UuidPojoInformation("Katherine Johnson", 26, 95000.0)));

        ArrayList<Information> allInformation = new ArrayList<>();
        allInformation.addAll(pojos);
        allInformation.addAll(records);
        allInformation.addAll(managers);
        allInformation.addAll(developers);
        allInformation.addAll(uuidPojos);

        GenericShowcase<Information> showcase = new GenericShowcase<>();
        showcase.addAll(pojos);
        showcase.addAll(records);
        showcase.addAll(managers);
        showcase.addAll(developers);
        showcase.addAll(uuidPojos);
        showcase.display("All information through GenericShowcase<T>:");

        ArrayList<Object> destination = new ArrayList<>();
        showcase.copyTo(destination);
        GenericShowcase.displayWildcard("Wildcard collection <?>:", destination);
        GenericShowcase.displaySummaries(allInformation);

        System.out.println("Java 21 sequenced collection first: " + allInformation.getFirst());
        System.out.println("Java 21 sequenced collection last: " + allInformation.getLast());
        System.out.println("Generic bounded method largest(): " + GenericShowcase.largest(List.of(3, 9, 4)));

        allInformation.forEach(information -> System.out.println(
                "Java 21 record-pattern switch: " + describe(information)));
        System.out.println("Ordinal demo only: " + OrdinalEnum.THIRD + " has ordinal " + OrdinalEnum.THIRD.ordinal());
        System.out.println("Advanced enum: " + AdvanceEnum.MANAGER.getRole()
                + " salary=" + AdvanceEnum.MANAGER.getSalary());
    }

    private static String describe(Information information) {
        return switch (information) {
            case RecordInformation(String name, int age, double salary) ->
                    "%s is %d years old; salary %.2f".formatted(name, age, salary);
            case PojoInformation pojo -> pojo.summary();
            case UuidPojoInformation uuidPojo -> uuidPojo.summary();
            case Staff staff -> staff.summary();
        };
    }
}