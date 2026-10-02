package com.fp.coding;

/** Console entry point for managing departments, staff, projects, and assignments. */
public final class Main {
    private final ConsoleContext context = new ConsoleContext();
    private final DepartmentConsole departments = new DepartmentConsole(context);
    private final StaffConsole staff = new StaffConsole(context);
    private final ProjectConsole projects = new ProjectConsole(context);

    private Main() {
    }

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            System.out.println("\nDepartment and project management");
            System.out.println("1. Departments  2. Staff  3. Projects  0. Exit");
            switch (context.readInt("Choose a section: ")) {
                case 1 -> departments.runMenu();
                case 2 -> staff.runMenu();
                case 3 -> projects.runMenu();
                case 0 -> running = false;
                default -> System.out.println("Choose 0, 1, 2, or 3.");
            }
        }
        System.out.println("Goodbye.");
    }
}
