package com.fp.coding;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Shared sealed base for managers, developers, and project owners.
 *
 * <p>
 * Names are normalized by {@link NameValidation}; salary must be finite and
 * nonnegative. The permitted
 * subclasses are explicit so the domain's staff roles remain closed to unknown
 * variants.
 */
@SuperBuilder
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public sealed abstract class Staff implements Information permits Manager, Developer, ProjectOwner {
    private final String name;
    private final double annualSalary;

    /** @return normalized staff name */
    public String getName() {
        return name;
    }

    /** @return annual salary in the model's currency units */
    public double getAnnualSalary() {
        return annualSalary;
    }

    /**
     * Creates a staff member after normalizing the name and validating salary.
     *
     * @param name         nonblank staff name
     * @param annualSalary finite, nonnegative annual salary
     * @throws IllegalArgumentException if the name is invalid or salary is
     *                                  negative/non-finite
     */
    protected Staff(String name, double annualSalary) {
        this.name = NameValidation.normalize(name);
        if (!Double.isFinite(annualSalary) || annualSalary < 0) {
            throw new IllegalArgumentException("annualSalary must be finite and 0 or above");
        }
        this.annualSalary = annualSalary;
    }

    /**
     * Returns the display name of this staff role.
     *
     * @return role title used in summaries
     */
    public abstract String roleTitle();

    /**
     * Returns the role, normalized name, and annual salary as readable text.
     *
     * @return formatted staff summary
     */
    public final String summary() {
        return "%s{name='%s', annualSalary=%.2f}".formatted(roleTitle(), name, annualSalary);
    }
}
