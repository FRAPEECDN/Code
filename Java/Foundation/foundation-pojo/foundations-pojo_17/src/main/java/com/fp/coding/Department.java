package com.fp.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Data;

/**
 * Owns one department's required manager and project owner, assigned
 * developers, and projects.
 *
 * <p>
 * Membership and assignment checks use object identity: an assignment may
 * reference only the staff and project
 * instances currently held by this department. Removing a developer or project
 * also removes assignments that refer
 * to it. Collection accessors return immutable snapshots, not live views.
 */
@Data
public final class Department {
    private final String name;
    private final Manager manager;
    private final ProjectOwner projectOwner;
    private final List<DeveloperAssignment> developerAssignments = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<ProjectAssignment> projectAssignments = new ArrayList<>();

    /** Returns the department name with surrounding whitespace removed. */
    public String getName() {
        return name;
    }

    /** Returns the manager who is structurally required by this department. */
    public Manager getManager() {
        return manager;
    }

    /**
     * Creates a department with its required manager and project owner.
     *
     * @param name         nonblank department name; surrounding whitespace is
     *                     removed
     * @param manager      manager required by the department
     * @param projectOwner owner whose projects may be added to the department
     * @throws IllegalArgumentException if {@code name} is null or blank
     * @throws NullPointerException     if {@code manager} or {@code projectOwner}
     *                                  is null
     */
    @Builder
    public Department(String name, Manager manager, ProjectOwner projectOwner) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("department name must not be blank");
        }
        this.name = name.strip();
        this.manager = Objects.requireNonNull(manager, "manager");
        this.projectOwner = Objects.requireNonNull(projectOwner, "projectOwner");
    }

    /**
     * Adds a developer with this department's position and happiness metadata.
     *
     * <p>
     * Adding the same developer instance again is idempotent and returns its
     * existing assignment.
     *
     * @param developer developer to add
     * @param position  developer's level in this department
     * @param happiness developer's current happiness rating
     * @return the new or existing assignment for {@code developer}
     * @throws NullPointerException if an assignment value is null
     */
    public DeveloperAssignment addDeveloper(Developer developer, StaffLevel position, StaffContent happiness) {
        DeveloperAssignment assignment = new DeveloperAssignment(developer, position, happiness);
        DeveloperAssignment existing = developerAssignments.stream()
                .filter(item -> item.developer() == developer)
                .findFirst()
                .orElse(null);
        if (existing != null)
            return existing;
        developerAssignments.add(assignment);
        return assignment;
    }

    /**
     * Removes a developer and all project assignments referencing that developer
     * instance.
     *
     * @param developer developer instance to remove
     * @return {@code true} if the developer was a member and was removed
     */
    public boolean removeDeveloper(Developer developer) {
        boolean removed = developerAssignments.removeIf(assignment -> assignment.developer() == developer);
        if (removed) {
            projectAssignments.removeIf(assignment -> assignment.staff() == developer);
        }
        return removed;
    }

    /**
     * Removes a removable staff member; this model permits removing developers
     * only.
     *
     * @param staff staff member to remove
     * @return {@code true} if a developer was removed; {@code false} for a
     *         nonmember or required role
     */
    public boolean removeStaff(Staff staff) {
        return staff instanceof Developer developer && removeDeveloper(developer);
    }

    /**
     * Returns an immutable snapshot of developer positions and happiness ratings.
     *
     * @return detached, unmodifiable assignment list
     */
    public List<DeveloperAssignment> getDeveloperAssignments() {
        return List.copyOf(developerAssignments);
    }

    /**
     * Returns an immutable snapshot of this department's projects.
     *
     * @return detached, unmodifiable project list
     */
    public List<Project> getProjects() {
        return List.copyOf(projects);
    }

    /**
     * Returns an immutable snapshot of staff-to-project assignments.
     *
     * @return detached, unmodifiable assignment list
     */
    public List<ProjectAssignment> getProjectAssignments() {
        return List.copyOf(projectAssignments);
    }

    /**
     * Adds a project whose owner is the same instance as this department's project
     * owner.
     *
     * <p>
     * Adding the same project instance more than once has no additional effect.
     *
     * @param project project to add
     * @return the supplied project
     * @throws NullPointerException     if {@code project} is null
     * @throws IllegalArgumentException if the project belongs to a different owner
     *                                  instance
     */
    public Project addProject(Project project) {
        Objects.requireNonNull(project, "project");
        if (project.getProjectOwner() != projectOwner) {
            throw new IllegalArgumentException("project owner must match the department's project owner");
        }
        if (projects.stream().noneMatch(existing -> existing == project)) {
            projects.add(project);
        }
        return project;
    }

    /**
     * Removes a project and all assignments referencing that project instance.
     *
     * @param project project instance to remove
     * @return {@code true} if the project was a member and was removed
     */
    public boolean removeProject(Project project) {
        boolean removed = projects.removeIf(existing -> existing == project);
        if (removed) {
            projectAssignments.removeIf(assignment -> assignment.project() == project);
        }
        return removed;
    }

    /**
     * Assigns one current department project to one current staff member.
     *
     * <p>
     * The manager, project owner, and added developers are eligible staff.
     * Repeating the same object pair returns
     * the existing assignment rather than storing a duplicate.
     *
     * @param staff   current staff instance in this department
     * @param project current project instance in this department
     * @return the new or existing assignment
     * @throws NullPointerException     if either argument is null
     * @throws IllegalArgumentException if either instance is not currently a member
     */
    public ProjectAssignment assignProject(Staff staff, Project project) {
        Objects.requireNonNull(staff, "staff");
        Objects.requireNonNull(project, "project");
        if (getStaffMembers().stream().noneMatch(member -> member == staff)) {
            throw new IllegalArgumentException("staff member must belong to the department");
        }
        if (projects.stream().noneMatch(existing -> existing == project)) {
            throw new IllegalArgumentException("project must belong to the department");
        }
        ProjectAssignment assignment = new ProjectAssignment(staff, project);
        boolean alreadyAssigned = projectAssignments.stream()
                .anyMatch(existing -> existing.staff() == staff && existing.project() == project);
        if (!alreadyAssigned)
            projectAssignments.add(assignment);
        return assignment;
    }

    /**
     * Removes an assignment matching the supplied staff and project instances.
     *
     * @param staff   assigned staff instance
     * @param project assigned project instance
     * @return {@code true} if a matching assignment was removed
     */
    public boolean unassignProject(Staff staff, Project project) {
        return projectAssignments.removeIf(assignment -> assignment.staff() == staff
                && assignment.project() == project);
    }

    /**
     * Returns the required manager and project owner followed by all assigned
     * developers.
     *
     * @return detached, unmodifiable snapshot of current staff
     */
    public List<Staff> getStaffMembers() {
        ArrayList<Staff> staffMembers = new ArrayList<>(developerAssignments.size() + 2);
        staffMembers.add(manager);
        staffMembers.add(projectOwner);
        developerAssignments.stream()
                .map(DeveloperAssignment::developer)
                .forEach(staffMembers::add);
        return List.copyOf(staffMembers);
    }

    /**
     * Sums annual salaries for the manager, project owner, and current developers.
     *
     * @return annual payroll total
     */
    public double getAnnualPayroll() {
        return getStaffMembers().stream().mapToDouble(Staff::getAnnualSalary).sum();
    }

    /**
     * Holds a developer's position and happiness rating within this department.
     *
     * @param developer assigned developer
     * @param position  department-specific role level
     * @param happiness department-specific happiness rating
     */
    public record DeveloperAssignment(Developer developer, StaffLevel position, StaffContent happiness) {
        public DeveloperAssignment {
            Objects.requireNonNull(developer, "developer");
            Objects.requireNonNull(position, "position");
            Objects.requireNonNull(happiness, "happiness");
        }
    }

    /**
     * Links one current department staff member to one current department project.
     *
     * @param staff   assigned staff member
     * @param project assigned project
     */
    public record ProjectAssignment(Staff staff, Project project) {
        public ProjectAssignment {
            Objects.requireNonNull(staff, "staff");
            Objects.requireNonNull(project, "project");
        }
    }
}
