package com.fp.coding;

import java.util.List;

/** Console actions for assigning and unassigning staff from department projects. */
final class AssignmentConsole {
    private final ConsoleContext context;

    AssignmentConsole(ConsoleContext context) {
        this.context = context;
    }

    void runMenu() {
        boolean returning = false;
        while (!returning) {
            System.out.println("\nAssignments: 1. Assign project  2. Unassign project  0. Back");
            switch (context.readInt("Choose an action: ")) {
                case 1 -> assignProject();
                case 2 -> unassignProject();
                case 0 -> returning = true;
                default -> System.out.println("Unknown action.");
            }
        }
    }

    private void assignProject() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department == null) return;
        Staff staff = context.select("staff member", department.getStaffMembers(), Staff::summary);
        Project project = context.select("project", department.getProjects(), context::projectLabel);
        if (staff != null && project != null) {
            department.assignProject(staff, project);
            System.out.println("Assigned " + project.getProjectName() + " to " + staff.getName() + ".");
        }
    }

    private void unassignProject() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department == null) return;
        List<Department.ProjectAssignment> assignments = department.getProjectAssignments();
        Department.ProjectAssignment assignment = context.select("assignment", assignments,
                item -> item.staff().getName() + " -> " + item.project().getProjectName());
        if (assignment != null && department.unassignProject(assignment.staff(), assignment.project())) {
            System.out.println("Removed the project assignment.");
        }
    }
}
