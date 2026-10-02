package com.fp.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Data;

/** Owns a department's manager, project owner, developers, and projects. */
@Data
public final class Department {
    private final String name;
    private final Manager manager;
    private final ProjectOwner projectOwner;
    private final List<DeveloperAssignment> developerAssignments = new ArrayList<>();
    private final List<Project> projects = new ArrayList<>();
    private final List<ProjectAssignment> projectAssignments = new ArrayList<>();

    /** Returns the normalized department name. */
    public String getName() {
        return name;
    }

    /** Returns the department's required manager. */
    public Manager getManager() {
        return manager;
    }

    /** Creates a department with its required manager and project owner. */
    @Builder
    public Department(String name, Manager manager, ProjectOwner projectOwner) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("department name must not be blank");
        }
        this.name = name.strip();
        this.manager = Objects.requireNonNull(manager, "manager");
        this.projectOwner = Objects.requireNonNull(projectOwner, "projectOwner");
    }

    /** Adds a developer with a department level and happiness rating. */
    public DeveloperAssignment addDeveloper(Developer developer, StaffLevel position, StaffContent happiness) {
        DeveloperAssignment assignment = new DeveloperAssignment(developer, position, happiness);
        DeveloperAssignment existing = developerAssignments.stream()
                .filter(item -> item.developer() == developer)
                .findFirst()
                .orElse(null);
        if (existing != null) return existing;
        developerAssignments.add(assignment);
        return assignment;
    }

    /** Removes a developer and any project assignments belonging to them. */
    public boolean removeDeveloper(Developer developer) {
        boolean removed = developerAssignments.removeIf(assignment -> assignment.developer() == developer);
        if (removed) {
            projectAssignments.removeIf(assignment -> assignment.staff() == developer);
        }
        return removed;
    }

    /** Removes a removable staff member; the required manager and owner cannot be removed. */
    public boolean removeStaff(Staff staff) {
        return staff instanceof Developer developer && removeDeveloper(developer);
    }

    /** Returns a read-only snapshot of developer positions and ratings. */
    public List<DeveloperAssignment> getDeveloperAssignments() {
        return List.copyOf(developerAssignments);
    }

    /** Returns a read-only snapshot of department projects. */
    public List<Project> getProjects() {
        return List.copyOf(projects);
    }

    /** Returns a read-only snapshot of staff-to-project assignments. */
    public List<ProjectAssignment> getProjectAssignments() {
        return List.copyOf(projectAssignments);
    }

    /** Adds a project owned by this department's project owner. */
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

    /** Removes a project and any department assignments that refer to it. */
    public boolean removeProject(Project project) {
        boolean removed = projects.removeIf(existing -> existing == project);
        if (removed) {
            projectAssignments.removeIf(assignment -> assignment.project() == project);
        }
        return removed;
    }

    /** Assigns a department project to one of its current staff members. */
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
        if (!alreadyAssigned) projectAssignments.add(assignment);
        return assignment;
    }

    /** Removes an existing staff-to-project assignment. */
    public boolean unassignProject(Staff staff, Project project) {
        return projectAssignments.removeIf(assignment -> assignment.staff() == staff
                && assignment.project() == project);
    }

    /** Returns the required manager and owner together with assigned developers. */
    public List<Staff> getStaffMembers() {
        ArrayList<Staff> staffMembers = new ArrayList<>(developerAssignments.size() + 2);
        staffMembers.add(manager);
        staffMembers.add(projectOwner);
        developerAssignments.stream()
                .map(DeveloperAssignment::developer)
                .forEach(staffMembers::add);
        return List.copyOf(staffMembers);
    }

    /** Sums annual salaries for all current department staff. */
    public double getAnnualPayroll() {
        return getStaffMembers().stream().mapToDouble(Staff::getAnnualSalary).sum();
    }

    /** Holds a developer's role level and happiness rating within this department. */
    public record DeveloperAssignment(Developer developer, StaffLevel position, StaffContent happiness) {
        public DeveloperAssignment {
            Objects.requireNonNull(developer, "developer");
            Objects.requireNonNull(position, "position");
            Objects.requireNonNull(happiness, "happiness");
        }
    }

    /** Links one current department staff member to one current department project. */
    public record ProjectAssignment(Staff staff, Project project) {
        public ProjectAssignment {
            Objects.requireNonNull(staff, "staff");
            Objects.requireNonNull(project, "project");
        }
    }
}
