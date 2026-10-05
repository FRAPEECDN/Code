package com.fp.coding;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Staff role that owns and registers the projects it sponsors.
 *
 * <p>
 * The project list is maintained in memory. Projects are registered by object
 * identity when created, and callers
 * receive an immutable snapshot rather than the mutable backing list.
 */
@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class ProjectOwner extends Staff {
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final List<Project> projects = new ArrayList<>();

    /**
     * Creates an owner with a normalized name and validated annual salary.
     *
     * @param name         nonblank owner name
     * @param annualSalary finite, nonnegative annual salary
     * @throws IllegalArgumentException if the name or salary is invalid
     */
    public ProjectOwner(String name, double annualSalary) {
        super(name, annualSalary);
        this.projects = new ArrayList<>();
    }

    /**
     * Returns an immutable snapshot of the projects registered to this owner.
     *
     * @return detached, unmodifiable project list
     */
    public List<Project> getProjects() {
        return List.copyOf(projects);
    }

    /**
     * Registers a project created with this owner.
     *
     * @param project project to register
     * @throws NullPointerException     if {@code project} is null
     * @throws IllegalArgumentException if the project refers to a different owner
     *                                  instance
     */
    void registerProject(Project project) {
        if (project.getProjectOwner() != this) {
            throw new IllegalArgumentException("project must belong to this project owner");
        }
        if (projects.stream().noneMatch(existing -> existing == project)) {
            projects.add(project);
        }
    }

    @Override
    /** {@inheritDoc} */
    public String roleTitle() {
        return "ProjectOwner";
    }
}
