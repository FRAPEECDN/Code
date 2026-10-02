package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;

class PojoModelTest {
    @Test
    void departmentTracksManagerAndDeveloper() {
        Manager manager = new Manager("Alex", 120_000.0, 8);
        ProjectOwner projectOwner = new ProjectOwner("Morgan", 40_000.0);
        Developer developer = new Developer("Jamie", 90_000.0, "backend");

        Department department = Department.builder()
            .name("Platform")
            .manager(manager)
            .projectOwner(projectOwner)
            .build();
        Department.DeveloperAssignment assignment = department.addDeveloper(
                developer,
                StaffLevel.SENIOR,
                StaffContent.HAPPY);

        assertEquals("Platform", department.getName());
        assertEquals(manager, department.getManager());
        assertEquals(projectOwner, department.getProjectOwner());
        assertEquals(1, department.getDeveloperAssignments().size());
        assertEquals(developer, assignment.developer());
        assertEquals(3, department.getStaffMembers().size());
        assertEquals(250_000.0, department.getAnnualPayroll());
        assertEquals("Developer", developer.roleTitle());
        assertNotNull(developer.getSpecialty());
        assertEquals("ProjectOwner", projectOwner.roleTitle());
    }

        @Test
        void departmentManagesStaffProjectsAndAssignmentsByIdentity() {
        ProjectOwner projectOwner = new ProjectOwner("Morgan", 40_000.0);
        Developer developer = new Developer("Jamie", 90_000.0, "backend");
        Department department = Department.builder()
            .name("Platform")
            .manager(new Manager("Alex", 120_000.0, 8))
            .projectOwner(projectOwner)
            .build();
        LocalDate registeredOn = LocalDate.of(2026, 10, 1);
        Project project = createProject(projectOwner, "Migration", registeredOn);
        Developer equalDeveloper = new Developer("Jamie", 90_000.0, "backend");
        Project equalProject = createProject(projectOwner, "Migration", registeredOn);

        department.addDeveloper(developer, StaffLevel.SENIOR, StaffContent.HAPPY);
        department.addProject(project);
        department.assignProject(developer, project);
        department.assignProject(developer, project);
        assertEquals(1, department.getProjectAssignments().size());

        department.addDeveloper(equalDeveloper, StaffLevel.SENIOR, StaffContent.HAPPY);
        department.addProject(equalProject);
        department.assignProject(equalDeveloper, equalProject);
        assertEquals(2, department.getProjectAssignments().size());
        assertThrows(IllegalArgumentException.class,
            () -> department.assignProject(new Developer("Outside", 1.0, "other"), project));
        assertThrows(IllegalArgumentException.class,
            () -> department.assignProject(developer, createProject(projectOwner, "Outside", registeredOn)));

        assertTrue(department.unassignProject(developer, project));
        assertFalse(department.unassignProject(developer, project));
        department.assignProject(developer, project);
        assertTrue(department.removeStaff(developer));
        assertEquals(1, department.getProjectAssignments().size());
        assertEquals(3, department.getStaffMembers().size());
        assertTrue(department.removeProject(project));
        assertTrue(department.removeProject(equalProject));
        assertTrue(department.getProjectAssignments().isEmpty());
        assertTrue(department.getProjects().isEmpty());
        }

    @Test
    void projectTracksTransitionsAndLinksToOwnerAndDepartment() {
        ProjectOwner projectOwner = new ProjectOwner("Morgan", 40_000.0);
        Department department = Department.builder()
                .name("Platform")
                .manager(new Manager("Alex", 120_000.0, 8))
                .projectOwner(projectOwner)
                .build();
        LocalDate registeredOn = LocalDate.of(2026, 10, 1);
        Project project = createProject(projectOwner, "Migration", registeredOn);
        Project secondProject = createProject(projectOwner, "Billing", registeredOn);

        department.addProject(project);
        department.addProject(secondProject);
        LocalDate startDate = LocalDate.of(2026, 12, 28);
        project.transitionTo(ProjectStatus.PLANNED, startDate);
        project.transitionTo(ProjectStatus.IMPLEMENTATING, startDate.plusDays(2));
        project.transitionTo(ProjectStatus.FINISHED, startDate.plusDays(5));

        assertEquals(ProjectStatus.FINISHED, project.getStatus());
        assertEquals(500_000.0, project.getBudget());
        assertEquals(startDate, project.getStartDate());
        assertEquals(LocalDate.of(2027, 3, 26), project.getDeadline());
        assertEquals(Map.of(
                ProjectStatus.REGISTERED, registeredOn,
            ProjectStatus.PLANNED, startDate,
            ProjectStatus.IMPLEMENTATING, startDate.plusDays(2),
            ProjectStatus.FINISHED, startDate.plusDays(5)), project.getStatusHistory());
        assertThrows(UnsupportedOperationException.class,
            () -> project.getStatusHistory().put(ProjectStatus.CANCEL, startDate.plusDays(6)));
        assertEquals(2, projectOwner.getProjects().size());
        assertEquals(2, department.getProjects().size());
    }

    @Test
    void projectAllowsCancellationAndRejectsInvalidTransitions() {
        ProjectOwner projectOwner = new ProjectOwner("Morgan", 40_000.0);
        LocalDate registeredOn = LocalDate.of(2026, 10, 1);
        Project project = createProject(projectOwner, "Cancelled", registeredOn);

        assertThrows(IllegalStateException.class,
                () -> project.transitionTo(ProjectStatus.FINISHED, registeredOn.plusDays(1)));
        assertThrows(IllegalArgumentException.class,
                () -> project.transitionTo(ProjectStatus.CANCEL, registeredOn.minusDays(1)));
        project.transitionTo(ProjectStatus.CANCEL, registeredOn.plusDays(1));
        assertEquals(ProjectStatus.CANCEL, project.getStatus());
        assertThrows(IllegalStateException.class,
            () -> project.transitionTo(ProjectStatus.PLANNED, registeredOn.plusDays(2)));
        }

        @Test
        void projectCanBeCancelledFromPlannedOrImplementating() {
        ProjectOwner projectOwner = new ProjectOwner("Morgan", 40_000.0);
        LocalDate registeredOn = LocalDate.of(2026, 10, 1);
        LocalDate startDate = LocalDate.of(2026, 12, 28);
        Project plannedProject = createProject(projectOwner, "Planned cancellation", registeredOn);
        Project implementingProject = createProject(projectOwner, "Implementation cancellation", registeredOn);

        plannedProject.transitionTo(ProjectStatus.PLANNED, startDate);
        plannedProject.transitionTo(ProjectStatus.CANCEL, startDate.plusDays(1));
        implementingProject.transitionTo(ProjectStatus.PLANNED, startDate);
        implementingProject.transitionTo(ProjectStatus.IMPLEMENTATING, startDate.plusDays(1));
        implementingProject.transitionTo(ProjectStatus.CANCEL, startDate.plusDays(2));

        assertEquals(ProjectStatus.CANCEL, plannedProject.getStatus());
        assertEquals(ProjectStatus.CANCEL, implementingProject.getStatus());
    }

    private Project createProject(ProjectOwner owner, String name, LocalDate registeredOn) {
        return Project.builder()
                .projectOwner(owner)
                .projectName(name)
                .sponsor("Operations")
                .budget(500_000.0)
                .startDate(LocalDate.of(2026, 12, 28))
                .deadline(LocalDate.of(2027, 3, 26))
                .registeredOn(registeredOn)
                .build();
    }
}
