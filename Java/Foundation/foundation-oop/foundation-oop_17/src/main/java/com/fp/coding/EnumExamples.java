package com.fp.coding;

/** Runnable section demonstrating basic, data-bearing, and string-valued enums. */
public final class EnumExamples {
    private EnumExamples() {
    }

    /** Runs the enum examples. */
    public static void main(String[] args) {
        System.out.println("Data-bearing role enum:");
        for (AdvanceEnum role : AdvanceEnum.values()) {
            System.out.printf("%s: level %d, salary %.2f%n",
                    role.getRole(), role.getLevel(), role.getSalary());
        }

        System.out.println("Declaration-order enum (ordinal is not a persistent ID):");
        for (OrdinalEnum position : OrdinalEnum.values()) {
            System.out.printf("%s has ordinal %d%n", position, position.ordinal());
        }

        System.out.println("String-valued enum lookup: "
                + StringEnum.fromValue("happy").getValue());
    }
}