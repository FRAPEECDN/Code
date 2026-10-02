package com.frapee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Console launcher for the Foundation examples.
 */
public final class App {

    private static final int EXIT_OPTION = 0;
    private static final int MAX_OPTION = 20;

    public App() {
    }

    public static void main(String[] args) {
        App app = new App();
        try (Scanner scanner = new Scanner(System.in)) {
            app.runMenu(scanner);
        }
    }

    private void runMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            printMenu();
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim();
            try {
                running = runSelection(Integer.parseInt(input));
            } catch (NumberFormatException exception) {
                System.out.println("Please select a number from the menu.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("Java Foundations");
        System.out.println("0. Exit");
        System.out.println("1. Primitives");
        System.out.println("2. Boxing");
        System.out.println("3. Basics and control flow");
        System.out.println("4. Arrays and algorithms");
        System.out.println("5. Async and concurrency");
        System.out.println("6. Custom callback");
        System.out.println("7. Comparable and Comparator");
        System.out.println("8. Functional interfaces");
        System.out.println("9. Lambdas");
        System.out.println("10. Strings");
        System.out.println("11. Operators");
        System.out.println("12. Null handling");
        System.out.println("13. Exceptions");
        System.out.println("14. Methods");
        System.out.println("15. Dates and time");
        System.out.println("16. Files and resources");
        System.out.println("17. Annotations");
        System.out.println("18. String encoding");
        System.out.println("19. Run all examples");
        System.out.print("Select an example: ");
    }

    /**
     * Run one menu selection. This is separate from Scanner input for testability.
     * @param option menu option
     * @return false when the application should exit
     */
    public boolean runSelection(int option) {
        if (option == EXIT_OPTION) {
            return false;
        }
        switch (option) {
            case 1 -> runPrimitives();
            case 2 -> runBoxing();
            case 3 -> runBasics();
            case 4 -> runArrays();
            case 5 -> runAsync();
            case 6 -> runCallbacks();
            case 7 -> runComparison();
            case 8 -> runFunctional();
            case 9 -> runLambdas();
            case 10 -> runStrings();
            case 11 -> runOperators();
            case 12 -> runNulls();
            case 13 -> runExceptions();
            case 14 -> runMethods();
            case 15 -> runDates();
            case 16 -> runFiles();
            case 17 -> runAnnotations();
            case 18 -> runEncoding();
            case 19 -> runAll();
            default -> System.out.println("Unknown selection: " + option);
        }
        return true;
    }

    private void runPrimitives() {
        FoundationPrimitives primitives = new FoundationPrimitives();
        System.out.println("int=" + primitives.intValue() + ", char=" + primitives.charValue()
            + ", boolean=" + primitives.booleanValue());
    }

    private void runBoxing() {
        FoundationBoxing boxing = new FoundationBoxing();
        Integer boxed = boxing.boxInt(42);
        System.out.println("boxed=" + boxed + ", unboxed=" + boxing.unboxInt(boxed));
    }

    private void runBasics() {
        FoundationBasics basics = new FoundationBasics();
        System.out.println("if=" + basics.ifMulti(1, 2) + ", switch=" + basics.switchYield(1)
            + ", factorial=" + basics.recursiveFactorial(5));
    }

    private void runArrays() {
        FoundationArray arrays = new FoundationArray();
        int[] values = {4, 1, 3, 2};
        arrays.arraysParallelSort(values);
        System.out.println(Arrays.toString(values));
    }

    private void runAsync() {
        FoundationAsync async = new FoundationAsync();
        System.out.println("virtual-thread factorial=" + async.useVirtualThreadExecutor(5));
    }

    private void runCallbacks() {
        FoundationCallback callbacks = new FoundationCallback(new RangeCallbackImplementation());
        System.out.println("callback result=" + callbacks.runCallback(4, 6));
    }

    private void runComparison() {
        FoundationComparison comparison = new FoundationComparison();
        System.out.println(comparison.sortByLengthThenName(Arrays.asList("pear", "fig", "apple")));
    }

    private void runFunctional() {
        FoundationFunctional functional = new FoundationFunctional();
        System.out.println("function result=" + functional.applyFunction(5, value -> value * 2));
    }

    private void runLambdas() {
        FoundationLambda lambdas = new FoundationLambda();
        System.out.println("lambda map=" + lambdas.mapValues(Arrays.asList(1, 2, 3), value -> value * 2));
    }

    private void runStrings() {
        FoundationStrings strings = new FoundationStrings();
        System.out.println(strings.buildText("name", "Java", 21));
    }

    private void runOperators() {
        FoundationOperators operators = new FoundationOperators();
        System.out.println("bitwise=" + operators.bitwiseAnd(6, 3)
            + ", shift=" + operators.leftShift(1, 3));
    }

    private void runNulls() {
        FoundationNull nulls = new FoundationNull();
        System.out.println(nulls.defaultIfNull(null, "fallback"));
    }

    private void runExceptions() {
        FoundationExceptions exceptions = new FoundationExceptions();
        System.out.println("parsed=" + exceptions.parseInteger("42")
            + ", invalid=" + exceptions.parseInteger("bad"));
    }

    private void runMethods() {
        FoundationMethods methods = new FoundationMethods();
        System.out.println("varargs sum=" + methods.sum(1, 2, 3)
            + ", overload=" + methods.describe(21));
    }

    private void runDates() {
        FoundationDates dates = new FoundationDates();
        System.out.println(dates.formatDate(dates.createDate(2026, 9, 27), "yyyy-MM-dd"));
    }

    private void runFiles() {
        FoundationFiles files = new FoundationFiles();
        try {
            Path directory = Files.createTempDirectory("foundations-app");
            Path file = files.resolve(directory, "example.txt");
            try {
                files.writeTextWithResource(file, "file example");
                System.out.println(files.readTextWithResource(file));
            } finally {
                files.deleteIfExists(file);
                files.deleteIfExists(directory);
            }
        } catch (IOException exception) {
            System.out.println("File example failed: " + exception.getMessage());
        }
    }

    private void runAnnotations() {
        FoundationAnnotations annotations = new FoundationAnnotations();
        System.out.println(annotations.suppressedWarningExample());
    }

    private void runEncoding() {
        System.out.println(FoundationEncodingStrings.encodeToNumberLowerAlpha("java"));
    }

    private void runAll() {
        for (int option = 1; option < MAX_OPTION - 1; option++) {
            System.out.println("\n--- Example " + option + " ---");
            runSelection(option);
        }
    }
}
