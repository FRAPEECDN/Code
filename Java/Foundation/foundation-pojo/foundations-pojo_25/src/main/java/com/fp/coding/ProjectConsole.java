package com.fp.coding;

import java.time.LocalDate;
import java.util.List;

/** Console actions for creating projects and adding or removing them in departments. */
final class ProjectConsole {
    private final ConsoleContext context;
    private final AssignmentConsole assignments;

    ProjectConsole(ConsoleContext context) {
        this.context = context;
        this.assignments = new AssignmentConsole(context);
    }

    void runMenu() {
        boolean returning = false;
        while (!returning) {
            System.out.println("\nProjects: 1. List directory  2. Create  3. Add to department"
                    + "  4. Remove from department  5. Assignments  0. Back");
            switch (context.readInt("Choose an action: ")) {
                case 1 -> context.projectDirectory.forEach(this::printProject);
                case 2 -> createProject();
                case 3 -> addProjectToDepartment();
                case 4 -> removeProjectFromDepartment();
                case 5 -> assignments.runMenu();
                case 0 -> returning = true;
                default -> System.out.println("Unknown action.");
            }
        }
    }

    private void createProject() {
        List<ProjectOwner> owners = context.staffDirectory.stream()
                .filter(ProjectOwner.class::isInstance).map(ProjectOwner.class::cast).toList();
        ProjectOwner owner = context.select("project owner", owners, Staff::summary);
        if (owner == null) return;
        LocalDate startDate = context.readDate("Start date (YYYY-MM-DD): ");
        LocalDate deadline = context.readDate("Deadline (YYYY-MM-DD): ");
        Project project = Project.builder()
                .projectOwner(owner)
                .projectName(context.readText("Project name: "))
                .sponsor(context.readText("Sponsor: "))
                .budget(context.readDouble("Budget: "))
                .startDate(startDate)
                .deadline(deadline)
                .registeredOn(LocalDate.now())
                .build();
        context.projectDirectory.add(project);
        System.out.println("Created " + context.projectLabel(project) + ".");
    }

    private void addProjectToDepartment() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department == null) return;
        List<Project> available = context.projectDirectory.stream()
                .filter(project -> project.getProjectOwner() == department.getProjectOwner())
                .filter(project -> department.getProjects().stream().noneMatch(existing -> existing == project))
                .toList();
        Project project = context.select("available project", available, context::projectLabel);
        if (project != null) {
            department.addProject(project);
            System.out.println("Added project to " + department.getName() + ".");
        }
    }

    private void removeProjectFromDepartment() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department == null) return;
        Project project = context.select("project", department.getProjects(), context::projectLabel);
        if (project != null && department.removeProject(project)) {
            System.out.println("Removed project from " + department.getName() + ".");
        }
    }

    private void printProject(Project project) {
        System.out.println(context.projectLabel(project) + " | Owner: " + project.getProjectOwner().getName()
                + " | Sponsor: " + project.getSponsor() + " | Budget: " + project.getBudget());
    }
}
