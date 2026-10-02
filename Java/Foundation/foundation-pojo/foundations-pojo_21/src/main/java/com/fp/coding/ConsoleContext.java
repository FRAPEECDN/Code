package com.fp.coding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/** Shared in-memory data and validated console input for the application sections. */
final class ConsoleContext {
    final Scanner input = new Scanner(System.in);
    final List<Department> departments = new ArrayList<>();
    final List<Staff> staffDirectory = new ArrayList<>();
    final List<Project> projectDirectory = new ArrayList<>();

    int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readText(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    double readDouble(String prompt) {
        while (true) {
            try {
                double value = Double.parseDouble(readText(prompt));
                if (Double.isFinite(value) && value >= 0) return value;
            } catch (NumberFormatException exception) {
                System.out.println("Enter a finite number that is 0 or greater.");
                continue;
            }
            System.out.println("Enter a finite number that is 0 or greater.");
        }
    }

    LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(readText(prompt));
            } catch (RuntimeException exception) {
                System.out.println("Enter a valid date in YYYY-MM-DD format.");
            }
        }
    }

    String readText(String prompt) {
        System.out.print(prompt);
        return input.nextLine().strip();
    }

    boolean confirm(String prompt) {
        return readText(prompt).equalsIgnoreCase("y");
    }

    <T> T select(String label, List<T> options, Function<T, String> display) {
        if (options.isEmpty()) {
            System.out.println("No " + label + " available.");
            return null;
        }
        for (int index = 0; index < options.size(); index++) {
            System.out.printf("%d. %s%n", index + 1, display.apply(options.get(index)));
        }
        while (true) {
            int choice = readInt("Select " + label + " (1-" + options.size() + "): ");
            if (choice >= 1 && choice <= options.size()) return options.get(choice - 1);
            System.out.println("Choose a listed number.");
        }
    }

    String projectLabel(Project project) {
        return project.getProjectName() + " [" + project.getStatus() + "]";
    }
}
