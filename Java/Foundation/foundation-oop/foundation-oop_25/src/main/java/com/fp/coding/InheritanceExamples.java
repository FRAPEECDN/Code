package com.fp.coding;

import java.util.List;

/** Runnable section demonstrating inheritance, overriding, and runtime polymorphism. */
public final class InheritanceExamples {
    private InheritanceExamples() {
    }

    /** Runs the sealed staff hierarchy example. */
    public static void main(String[] args) {
        List<Staff> team = List.of(
                new Manager("Ada Lovelace", 160000.0, 5),
                new Developer("Grace Hopper", 140000.0, "Compilers"));

        team.forEach(staff -> System.out.printf("%s: %s%n", staff.roleTitle(), staff.summary()));
    }
}