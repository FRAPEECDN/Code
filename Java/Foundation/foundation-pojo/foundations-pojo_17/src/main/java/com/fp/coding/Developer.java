package com.fp.coding;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Staff member with a required specialty such as backend or infrastructure. */
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class Developer extends Staff {
    private String specialty;

    /** @return specialty with surrounding whitespace removed */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Creates a developer with a normalized name, validated salary, and required
     * specialty.
     *
     * @param name         nonblank developer name
     * @param annualSalary finite, nonnegative annual salary
     * @param specialty    nonblank technical specialty; surrounding whitespace is
     *                     removed
     * @throws IllegalArgumentException if the name, salary, or specialty is invalid
     */
    public Developer(String name, double annualSalary, String specialty) {
        super(name, annualSalary);
        if (specialty == null || specialty.isBlank()) {
            throw new IllegalArgumentException("specialty must not be blank");
        }
        this.specialty = specialty.strip();
    }

    /** {@inheritDoc} */
    @Override
    public String roleTitle() {
        return "Developer";
    }
}
