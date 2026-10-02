package com.fp.coding;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/** Console launcher that groups every runnable example into a selectable section. */
public final class ExampleMenu {
    private static final List<Section> SECTIONS = List.of(
            new Section("1", "POJO and record models", () -> ModelExamples.main(new String[0])),
            new Section("2", "Enums", () -> EnumExamples.main(new String[0])),
            new Section("3", "Inheritance and polymorphism", () -> InheritanceExamples.main(new String[0])),
            new Section("4", "Generics and Java 21 language features", () -> GenericExamples.main(new String[0])),
            new Section("5", "Objects, Optional, and catalog composition",
                    () -> ObjectsOptionalExamples.main(new String[0])),
            new Section("6", "Department composition", () -> DepartmentExamples.main(new String[0])),
            new Section("7", "UUID, Comparable, Comparator, and serialization",
                    () -> UuidPojoExamples.main(new String[0])),
                new Section("8", "List, Set, Map, searching, and streams",
                    () -> CollectionExamples.main(new String[0])),
                new Section("9", "Java 21 sequenced collections",
                    () -> CollectionEvolutionExamples.main(new String[0])));

    private ExampleMenu() {
    }

    /** Starts the interactive menu using standard input and output. */
    public static void main(String[] args) {
        runMenu(new Scanner(System.in), System.out);
    }

    /**
     * Displays sections until the user selects exit or input ends.
     *
     * @param input source for menu choices
     * @param output destination for menu text
     * @throws NullPointerException if either argument is null
     */
    public static void runMenu(Scanner input, PrintStream output) {
        Objects.requireNonNull(input, "input");
        Objects.requireNonNull(output, "output");

        while (true) {
            printMenu(output);
            if (!input.hasNextLine()) {
                return;
            }

            String choice = input.nextLine().strip();
            if (choice.equals("0")) {
                output.println("Exiting examples.");
                return;
            }
            if (choice.equals("10")) {
                runAll(output);
                continue;
            }

            Section selected = SECTIONS.stream()
                    .filter(section -> section.number().equals(choice))
                    .findFirst()
                    .orElse(null);
            if (selected == null) {
                output.println("Choose a section from 1 to 9, 10 to run all, or 0 to exit.");
                continue;
            }
            runSection(selected, output);
        }
    }

    private static void printMenu(PrintStream output) {
        output.println();
        output.println("=== Foundation OOP Examples ===");
        SECTIONS.forEach(section -> output.printf("%s. %s%n", section.number(), section.title()));
        output.println("10. Run all sections");
        output.println("0. Exit");
        output.print("Select a section: ");
    }

    private static void runAll(PrintStream output) {
        SECTIONS.forEach(section -> runSection(section, output));
    }

    private static void runSection(Section section, PrintStream output) {
        output.printf("%n--- %s ---%n", section.title());
        try {
            section.action().run();
        } catch (Exception exception) {
            output.printf("Section failed: %s%n", exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface ExampleAction {
        void run() throws Exception;
    }

    private record Section(String number, String title, ExampleAction action) {
    }
}