package com.fp.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Department composition example with one manager and zero or more developers.
 *
 * <p>Each developer is stored with a department-specific {@link OrdinalEnum}
 * position and {@link StringEnum} happiness value. The assignment record keeps
 * those ratings with the relationship rather than on the developer itself.
 */
public final class Department {
    private final String name;
    private final Manager manager;
    private final List<DeveloperAssignment> developerAssignments = new ArrayList<>();

    /**
     * Creates a department with its required manager.
     *
     * @param name non-blank department name
     * @param manager the department's single manager
     * @throws IllegalArgumentException if the name is null or blank
     * @throws NullPointerException if {@code manager} is null
     */
    public Department(String name, Manager manager) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("department name must not be blank");
        }
        this.name = name.strip();
        this.manager = Objects.requireNonNull(manager, "manager");
    }

    /** @return this department's normalized name */
    public String getName() {
        return name;
    }

    /** @return this department's required manager */
    public Manager getManager() {
        return manager;
    }

    /**
     * Assigns a developer to this department with a position and happiness value.
     *
     * @param developer developer to assign
     * @param position enum position within the department
     * @param happiness enum happiness rating
     * @return the newly created assignment
     * @throws NullPointerException if any argument is null
     */
    public DeveloperAssignment addDeveloper(Developer developer, OrdinalEnum position, StringEnum happiness) {
        DeveloperAssignment assignment = new DeveloperAssignment(developer, position, happiness);
        developerAssignments.add(assignment);
        return assignment;
    }

    /**
     * Returns an immutable snapshot of developer assignments.
     *
     * @return assignments in insertion order
     */
    public List<DeveloperAssignment> getDeveloperAssignments() {
        return List.copyOf(developerAssignments);
    }

    /**
     * Returns the manager and assigned developers through their shared parent type.
     *
     * @return an immutable snapshot with the manager first
     */
    public List<Staff> getStaffMembers() {
        ArrayList<Staff> staffMembers = new ArrayList<>(developerAssignments.size() + 1);
        staffMembers.add(manager);
        developerAssignments.stream()
                .map(DeveloperAssignment::developer)
                .forEach(staffMembers::add);
        return List.copyOf(staffMembers);
    }

    /** @return the manager's salary plus the salaries of all assigned developers */
    public double getAnnualPayroll() {
        return getStaffMembers().stream().mapToDouble(Staff::getAnnualSalary).sum();
    }

    /**
     * Department-specific data associated with one developer assignment.
     *
     * @param developer assigned developer
     * @param position position represented by the ordinal enum
     * @param happiness happiness represented by the string-valued enum
     */
    public record DeveloperAssignment(Developer developer, OrdinalEnum position, StringEnum happiness) {
        /** Rejects missing developer or rating values. */
        public DeveloperAssignment {
            Objects.requireNonNull(developer, "developer");
            Objects.requireNonNull(position, "position");
            Objects.requireNonNull(happiness, "happiness");
        }
    }
}