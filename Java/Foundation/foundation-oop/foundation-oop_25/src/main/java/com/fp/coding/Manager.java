package com.fp.coding;

import java.util.Objects;

/**
 * Final leaf in the sealed staff hierarchy, with manager-specific team size data.
 *
 * <p>The Java 25 constructor validates its local team-size argument before calling
 * the parent constructor, demonstrating a flexible constructor body.
 */
public final class Manager extends Staff {
    private final int teamSize;

    /**
     * Creates a manager and rejects negative team sizes.
     *
     * @param name manager's name
     * @param annualSalary manager's annual salary
     * @param teamSize number of people on the team; must be zero or greater
     * @throws IllegalArgumentException if the team size or inherited staff data is invalid
     */
    public Manager(String name, double annualSalary, int teamSize) {
        if (teamSize < 0) {
            throw new IllegalArgumentException("teamSize must be 0 or above");
        }
        super(name, annualSalary);
        this.teamSize = teamSize;
    }

    /** @return the number of people on this manager's team */
    public int getTeamSize() {
        return teamSize;
    }

    @Override
    public String roleTitle() {
        return "Manager";
    }

    @Override
    public String toString() {
        return "%s[teamSize=%d]".formatted(summary(), teamSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), teamSize);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Manager other)) {
            return false;
        }
        return super.equals(other) && teamSize == other.teamSize;
    }
}