package com.fp.coding;

import java.util.List;

/** Console actions for creating staff and changing department membership. */
final class StaffConsole {
    private final ConsoleContext context;

    StaffConsole(ConsoleContext context) {
        this.context = context;
    }

    void runMenu() {
        boolean returning = false;
        while (!returning) {
            System.out.println("\nStaff: 1. List directory  2. Add staff  3. Add to department"
                    + "  4. Remove from department  0. Back");
            switch (context.readInt("Choose an action: ")) {
                case 1 -> context.staffDirectory.forEach(staff -> System.out.println(staff.summary()));
                case 2 -> addStaff();
                case 3 -> addStaffToDepartment();
                case 4 -> removeStaffFromDepartment();
                case 0 -> returning = true;
                default -> System.out.println("Unknown action.");
            }
        }
    }

    private void addStaff() {
        System.out.println("1. Manager  2. Developer  3. Project owner");
        String name = context.readText("Staff name: ");
        double salary = context.readDouble("Annual salary: ");
        Staff newStaff = switch (context.readInt("Role: ")) {
            case 1 -> new Manager(name, salary, context.readInt("Team size: "));
            case 2 -> new Developer(name, salary, context.readText("Specialty: "));
            case 3 -> new ProjectOwner(name, salary);
            default -> null;
        };
        if (newStaff == null) {
            System.out.println("Unknown role; no staff member was added.");
        } else {
            context.staffDirectory.add(newStaff);
            System.out.println("Added " + newStaff.summary() + ".");
        }
    }

    private void addStaffToDepartment() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department == null) return;
        List<Developer> developers = context.staffDirectory.stream()
                .filter(Developer.class::isInstance).map(Developer.class::cast)
                .filter(candidate -> department.getStaffMembers().stream().noneMatch(member -> member == candidate))
                .toList();
        Developer developer = context.select("available developer", developers, Staff::summary);
        if (developer != null) {
            department.addDeveloper(developer, StaffLevel.MID, StaffContent.NEUTRAL);
            System.out.println("Added developer to " + department.getName() + ".");
        }
    }

    private void removeStaffFromDepartment() {
        Department department = context.select("department", context.departments, Department::getName);
        if (department == null) return;
        Staff staff = context.select("staff member", department.getStaffMembers(), Staff::summary);
        if (staff == null) return;
        if (department.removeStaff(staff)) {
            System.out.println("Removed " + staff.getName() + " from " + department.getName() + ".");
        } else {
            System.out.println("A department's manager and project owner are required and cannot be removed.");
        }
    }
}
