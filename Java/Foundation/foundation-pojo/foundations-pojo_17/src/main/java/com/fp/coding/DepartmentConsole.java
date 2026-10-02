package com.fp.coding;

import java.util.ArrayList;
import java.util.List;

/** Console actions for creating, listing, and removing departments. */
final class DepartmentConsole {
    private final ConsoleContext context;

    DepartmentConsole(ConsoleContext context) {
        this.context = context;
    }

    void runMenu() {
        boolean returning = false;
        while (!returning) {
            System.out.println("\nDepartments: 1. List  2. Add  3. Remove  0. Back");
            switch (context.readInt("Choose an action: ")) {
                case 1 -> context.departments.forEach(this::printDepartment);
                case 2 -> addDepartment();
                case 3 -> removeDepartment();
                case 0 -> returning = true;
                default -> System.out.println("Unknown action.");
            }
        }
    }

    private void addDepartment() {
        List<Manager> managers = context.staffDirectory.stream()
                .filter(Manager.class::isInstance).map(Manager.class::cast).toList();
        List<ProjectOwner> owners = context.staffDirectory.stream()
                .filter(ProjectOwner.class::isInstance).map(ProjectOwner.class::cast).toList();
        Manager manager = context.select("manager", managers, Staff::summary);
        ProjectOwner owner = context.select("project owner", owners, Staff::summary);
        if (manager == null || owner == null) {
            System.out.println("Create a manager and a project owner in Staff before adding a department.");
            return;
        }
        Department department = Department.builder()
                .name(context.readText("Department name: "))
                .manager(manager)
                .projectOwner(owner)
                .build();
        context.departments.add(department);
        addExistingDevelopers(department);
        addExistingProjects(department);
        System.out.println("Added department " + department.getName() + ".");
    }

    private void addExistingDevelopers(Department department) {
        List<Developer> available = context.staffDirectory.stream()
                .filter(Developer.class::isInstance).map(Developer.class::cast)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        while (!available.isEmpty() && context.confirm("Add an existing developer? (y/n): ")) {
            Developer developer = context.select("developer", available, Staff::summary);
            if (developer != null) {
                department.addDeveloper(developer, StaffLevel.MID, StaffContent.NEUTRAL);
                available.remove(developer);
            }
        }
    }

    private void addExistingProjects(Department department) {
        List<Project> available = context.projectDirectory.stream()
                .filter(project -> project.getProjectOwner() == department.getProjectOwner())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        while (!available.isEmpty() && context.confirm("Add an existing project? (y/n): ")) {
            Project project = context.select("project", available, context::projectLabel);
            if (project != null) {
                department.addProject(project);
                available.remove(project);
            }
        }
    }

    private void removeDepartment() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department != null && context.departments.remove(department)) {
            System.out.println("Removed department " + department.getName() + ".");
        }
    }

    private void printDepartment(Department department) {
        System.out.println("\n" + department.getName() + " | Manager: " + department.getManager().getName()
                + " | Project owner: " + department.getProjectOwner().getName());
        department.getStaffMembers().forEach(staff -> System.out.println("  Staff: " + staff.summary()));
        department.getProjects().forEach(project -> System.out.println("  Project: " + context.projectLabel(project)));
        department.getProjectAssignments().forEach(assignment -> System.out.println("  Assigned: "
                + assignment.staff().getName() + " -> " + assignment.project().getProjectName()));
        System.out.printf("  Annual payroll: %.2f%n", department.getAnnualPayroll());
    }
}
