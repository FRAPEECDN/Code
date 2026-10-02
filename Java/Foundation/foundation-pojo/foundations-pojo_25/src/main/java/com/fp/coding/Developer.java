package com.fp.coding;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/** Staff member with a required specialty such as backend or infrastructure. */
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class Developer extends Staff {
    private String specialty;

    public String getSpecialty() {
        return specialty;
    }

    public Developer(String name, double annualSalary, String specialty) {
        super(name, annualSalary);
        if (specialty == null || specialty.isBlank()) {
            throw new IllegalArgumentException("specialty must not be blank");
        }
        this.specialty = specialty.strip();
    }

    @Override
    public String roleTitle() {
        return "Developer";
    }
}
