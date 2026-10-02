package com.fp.coding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Mutable JavaBean example implementing the shared {@link Information} contract.
 *
 * <p>The class demonstrates encapsulated fields, overloaded constructors, input
 * validation, value-based {@link #equals(Object)} and {@link #hashCode()}, and
 * copying. Salary is optional; date and snapshot values are required. The no-arg
 * constructor provides a valid inactive bean for frameworks and examples.
 */
public final class PojoInformation implements Information, Cloneable {
    private long id;
    private String name;
    private int age;
    private Double salary;
    private boolean active;
    private LocalDate createdDate;
    private LocalDateTime snapShot;

    /** Creates a valid inactive bean with placeholder identity data. */
    public PojoInformation() {
        this(0, "Unknown", 1, null, false, LocalDate.now(), LocalDateTime.now());
    }

    /**
     * Creates an active bean without a salary value.
     *
     * @param id identifier for this bean
    * @param name non-blank personal name; surrounding whitespace is removed
     * @param age positive age
     */
    public PojoInformation(long id, String name, int age) {
        this(id, name, age, null, true, LocalDate.now(), LocalDateTime.now());
    }

    /**
     * Creates an active bean with an optional, validated salary.
     *
     * @param id identifier for this bean
    * @param name non-blank personal name; surrounding whitespace is removed
     * @param age positive age
     * @param salary finite, non-negative salary, or {@code null} when unspecified
     */
    public PojoInformation(long id, String name, int age, Double salary) {
        this(id, name, age, salary, true, LocalDate.now(), LocalDateTime.now());
    }

    private PojoInformation(long id, String name, int age, Double salary, boolean active,
            LocalDate createdDate, LocalDateTime snapShot) {
        this.id = id;
        setName(name);
        setAge(age);
        setSalary(salary);
        this.active = active;
        this.createdDate = Objects.requireNonNull(createdDate, "createdDate");
        this.snapShot = Objects.requireNonNull(snapShot, "snapShot");
    }

    @Override
    public String summary() {
        return "PojoInformation{name='%s', age=%d, salary=%s}".formatted(name, age, salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, salary, active, createdDate, snapShot);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof PojoInformation other)) {
            return false;
        }
        return id == other.id
                && age == other.age
                && active == other.active
                && Objects.equals(name, other.name)
                && Objects.equals(salary, other.salary)
                && Objects.equals(createdDate, other.createdDate)
                && Objects.equals(snapShot, other.snapShot);
    }

    @Override
    public String toString() {
        return "PojoInformation[id=%d, name='%s', age=%d, salary=%s, active=%s, createdDate=%s, snapShot=%s]"
                .formatted(id, name, age, salary, active, createdDate, snapShot);
    }

    /** @return the identifier stored by this bean */
    public long getId() {
        return id;
    }

    /** @param id identifier to store */
    public void setId(long id) {
        this.id = id;
    }

    /** @return the validated name stored by this bean */
    public String getName() {
        return name;
    }

    /**
     * Replaces the name after checking it contains letters and whitespace only.
     *
    * @param name non-blank personal name; surrounding whitespace is removed
     * @throws IllegalArgumentException if the name is null, blank, or contains other characters
     */
    public void setName(String name) {
        this.name = NameValidation.normalize(name);
    }

    /** @return the positive age stored by this bean */
    public int getAge() {
        return age;
    }

    /**
     * Replaces the age.
     *
     * @param age positive age
     * @throws IllegalArgumentException if {@code age} is not positive
     */
    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("age must be greater than 0");
        }
        this.age = age;
    }

    /** @return the optional salary, or {@code null} when it is unspecified */
    public Double getSalary() {
        return salary;
    }

    /**
     * Replaces the optional salary.
     *
     * @param salary finite, non-negative salary, or {@code null} when unspecified
     * @throws IllegalArgumentException if a non-null salary is negative or non-finite
     */
    public void setSalary(Double salary) {
        if (salary != null && (!Double.isFinite(salary) || salary < 0)) {
            throw new IllegalArgumentException("salary must be finite and 0 or above");
        }
        this.salary = salary;
    }

    /** @return whether this bean is marked active */
    public boolean isActive() {
        return active;
    }

    /** @param active whether to mark this bean active */
    public void setActive(boolean active) {
        this.active = active;
    }

    /** @return the non-null date on which this bean was created */
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the creation date.
     *
     * @param createdDate non-null creation date
     * @throws NullPointerException if {@code createdDate} is null
     */
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = Objects.requireNonNull(createdDate, "createdDate");
    }

    /** @return the non-null snapshot timestamp */
    public LocalDateTime getSnapShot() {
        return snapShot;
    }

    /**
     * Sets the snapshot timestamp.
     *
     * @param snapShot non-null timestamp
     * @throws NullPointerException if {@code snapShot} is null
     */
    public void setSnapShot(LocalDateTime snapShot) {
        this.snapShot = Objects.requireNonNull(snapShot, "snapShot");
    }

    /**
     * Creates an independent bean containing the same field values.
     *
     * @return a copy of this object
     */
    public PojoInformation copy() {
        return new PojoInformation(id, name, age, salary, active, createdDate, snapShot);
    }

    @Override
    public PojoInformation clone() {
        return copy();
    }
}