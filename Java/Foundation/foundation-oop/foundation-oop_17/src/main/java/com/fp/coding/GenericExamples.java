package com.fp.coding;

import java.util.ArrayList;
import java.util.List;

/**
 * Runnable Java 17 tour of the project's enum, inheritance, record, and generics examples.
 *
 * <p>The demo first builds separately typed {@link ArrayList} instances, then adds
 * them to a common {@code Information} collection and to {@link GenericShowcase}.
 * It also demonstrates a switch expression and pattern matching with {@code instanceof}.
 */
public final class GenericExamples {
    private GenericExamples() {
    }

        /** Runs the Java 17 examples and prints their results to standard output.
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

                StringEnum mood = StringEnum.fromValue("happy");
                String moodDescription = describeMood(mood);
                System.out.printf("Java 17 switch expression: %s is %s%n", mood.getValue(), moodDescription);

                allInformation.forEach(information -> {
                        if (information instanceof PojoInformation pojo) {
                                System.out.println("Java 17 instanceof pattern: " + pojo.getName());
                        }
                });

                System.out.println("Generic bounded method largest(): " + GenericShowcase.largest(List.of(3, 9, 4)));
                System.out.println("Ordinal demo only: " + OrdinalEnum.THIRD + " has ordinal " + OrdinalEnum.THIRD.ordinal());
                System.out.println("Advanced enum: " + AdvanceEnum.MANAGER.getRole()
                                + " salary=" + AdvanceEnum.MANAGER.getSalary());
        }

        static String describeMood(StringEnum mood) {
                return switch (mood) {
            case HAPPY -> "positive";
            case SAD, MAD -> "unhappy";
            case OK, CHILL -> "calm";
        };
    }
}