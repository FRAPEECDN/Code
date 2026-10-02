package com.fp.coding;

/**
 * Enum example whose constants carry a role name, hierarchy level, and salary.
 *
 * <p>It demonstrates that an enum can hold immutable fields and expose behavior,
 * rather than serving only as a list of labels.
 */
public enum AdvanceEnum {
    /** Management role at level 1 with the example salary of 120,000. */
    MANAGER("Manager", 1, 120000.0),
    /** Software development role at level 2 with the example salary of 80,000. */
    DEVELOPER("Developer", 2, 80000.0),
    /** Software testing role at level 3 with the example salary of 60,000. */
    TESTER("Tester", 3, 60000.0),
    /** User-interface design role at level 4 with the example salary of 90,000. */
    DESIGNER("Designer", 4, 90000.0);

    private final String role;
    private final int level;
    private final double salary;

    AdvanceEnum(String role, int level, double salary) {
        this.role = role;
        this.level = level;
        this.salary = salary;
    }

    /** @return the display name associated with this role */
    public String getRole() {
        return role;
    }

    /** @return the example hierarchy level associated with this role */
    public int getLevel() {
        return level;
    }

    /** @return the example salary associated with this role */
    public double getSalary() {
        return salary;
    }
}