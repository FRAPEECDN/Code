package com.fp.coding;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Staff member responsible for a team, with a validated nonnegative team size.
 */
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Manager extends Staff {
    private int teamSize;

    /** @return number of people in the manager's team */
    public int getTeamSize() {
        return teamSize;
    }

    /**
     * Updates the team size.
     *
     * @param teamSize new nonnegative team size
     * @throws IllegalArgumentException if {@code teamSize} is negative
     */
    public void setTeamSize(int teamSize) {
        if (teamSize < 0) {
            throw new IllegalArgumentException("teamSize must be 0 or above");
        }
        this.teamSize = teamSize;
    }

    /**
     * Creates a manager with validated staff details and team size.
     *
     * @param name         nonblank manager name
     * @param annualSalary finite, nonnegative annual salary
     * @param teamSize     nonnegative number of direct reports
     * @throws IllegalArgumentException if staff details or team size are invalid
     */
    public Manager(String name, double annualSalary, int teamSize) {
        super(name, annualSalary);
        if (teamSize < 0) {
            throw new IllegalArgumentException("teamSize must be 0 or above");
        }
        this.teamSize = teamSize;
    }

    /** {@inheritDoc} */
    @Override
    public String roleTitle() {
        return "Manager";
    }
}
