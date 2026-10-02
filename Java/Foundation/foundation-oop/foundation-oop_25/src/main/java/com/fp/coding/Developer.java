package com.fp.coding;

import java.util.Objects;

/**
 * Developer branch of the staff hierarchy, left {@code non-sealed} for extension.
 *
 * <p>This child adds a specialty while inheriting the shared staff identity and
 * salary rules. Unlike {@link Manager}, other classes may subclass this type;
 * subclasses that add state should also extend the equality contract.
 */
public non-sealed class Developer extends Staff {
    private final String specialty;

    /**
     * Creates a developer with a required specialty.
     *
     * @param name developer's name
     * @param annualSalary developer's annual salary
     * @param specialty non-blank area of expertise
     * @throws IllegalArgumentException if the specialty or inherited staff data is invalid
     */
    public Developer(String name, double annualSalary, String specialty) {
        super(name, annualSalary);
        if (specialty == null || specialty.isBlank()) {
            throw new IllegalArgumentException("specialty must not be blank");
        }
        this.specialty = specialty.strip();
    }

    /** @return this developer's validated specialty */
    public String getSpecialty() {
        return specialty;
    }

    @Override
    public String roleTitle() {
        return "Developer";
    }

    @Override
    public String toString() {
        return "%s[specialty='%s']".formatted(summary(), specialty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), specialty);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Developer other = (Developer) obj;
        return super.equals(other) && Objects.equals(specialty, other.specialty);
    }
}