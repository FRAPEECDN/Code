package com.fp.coding;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Shared sealed base for managers, developers, and project owners. */
@SuperBuilder
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public sealed abstract class Staff implements Information permits Manager, Developer, ProjectOwner {
    private final String name;
    private final double annualSalary;

    public String getName() {
        return name;
    }

    public double getAnnualSalary() {
        return annualSalary;
    }

    protected Staff(String name, double annualSalary) {
        this.name = NameValidation.normalize(name);
        if (!Double.isFinite(annualSalary) || annualSalary < 0) {
            throw new IllegalArgumentException("annualSalary must be finite and 0 or above");
        }
        this.annualSalary = annualSalary;
    }

    /** Returns the display name of this staff role. */
    public abstract String roleTitle();

    /** Returns the role, normalized name, and annual salary as readable text. */
    public final String summary() {
        return "%s{name='%s', annualSalary=%.2f}".formatted(roleTitle(), name, annualSalary);
    }
}
