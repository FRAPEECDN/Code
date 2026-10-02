package com.fp.coding;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Builder;
import lombok.experimental.SuperBuilder;

/** Staff role that owns and registers the projects it sponsors. */
@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class ProjectOwner extends Staff {
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private final List<Project> projects = new ArrayList<>();

    public ProjectOwner(String name, double annualSalary) {
        super(name, annualSalary);
        this.projects = new ArrayList<>();
    }

    public List<Project> getProjects() {
        return List.copyOf(projects);
    }

    void registerProject(Project project) {
        if (project.getProjectOwner() != this) {
            throw new IllegalArgumentException("project must belong to this project owner");
        }
        if (projects.stream().noneMatch(existing -> existing == project)) {
            projects.add(project);
        }
    }

    @Override
    public String roleTitle() {
        return "ProjectOwner";
    }
}
