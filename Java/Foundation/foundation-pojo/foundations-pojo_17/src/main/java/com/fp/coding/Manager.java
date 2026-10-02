package com.fp.coding;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Staff member responsible for a team, with a validated team-size value. */
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Manager extends Staff {
    private int teamSize;

    public int getTeamSize() {
        return teamSize;
    }

    /** Updates the team size; negative values are rejected. */
    public void setTeamSize(int teamSize) {
        if (teamSize < 0) {
            throw new IllegalArgumentException("teamSize must be 0 or above");
        }
        this.teamSize = teamSize;
    }

    public Manager(String name, double annualSalary, int teamSize) {
        super(name, annualSalary);
        if (teamSize < 0) {
            throw new IllegalArgumentException("teamSize must be 0 or above");
        }
        this.teamSize = teamSize;
    }

    @Override
    public String roleTitle() {
        return "Manager";
    }
}
