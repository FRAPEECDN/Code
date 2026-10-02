package com.fp.coding;

import java.util.Objects;

/**
 * Sealed parent in the inheritance example, shared by the manager and developer types.
 *
 * <p>The hierarchy demonstrates a {@code final} child that closes inheritance and a
 * {@code non-sealed} child that permits further specialization. Common identity and
 * salary validation live here so both children inherit the same contract.
 */
public sealed abstract class Staff implements Information permits Manager, Developer {
    private final String name;
    private final double annualSalary;

    /**
     * Creates staff after validating the common name and annual salary.
     *
    * @param name non-blank personal name; surrounding whitespace is removed
     * @param annualSalary finite, non-negative annual salary
     * @throws IllegalArgumentException if either value is invalid
     */
    protected Staff(String name, double annualSalary) {
        this.name = NameValidation.normalize(name);
        if (!Double.isFinite(annualSalary) || annualSalary < 0) {
            throw new IllegalArgumentException("annualSalary must be finite and 0 or above");
        }
        this.annualSalary = annualSalary;
    }

    /** @return the validated name shared by all staff subtypes */
    public final String getName() {
        return name;
    }

    /** @return the finite, non-negative annual salary */
    public final double getAnnualSalary() {
        return annualSalary;
    }

    /**
     * Returns the role label supplied by the concrete child class.
     *
     * @return this staff member's role
     */
    public abstract String roleTitle();

    @Override
    public final String summary() {
        return "%s{name='%s', annualSalary=%.2f}".formatted(roleTitle(), name, annualSalary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), name, annualSalary);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && getClass() == obj.getClass()
                && name.equals(((Staff) obj).name)
                && Double.compare(annualSalary, ((Staff) obj).annualSalary) == 0);
    }
}