package com.fp.coding;

/** Runnable example of a department with one manager and multiple rated developers. */
public final class DepartmentExamples {
    private DepartmentExamples() {
    }

    /** Runs the department example and displays the assigned positions and happiness values.
     *
     * @param args command-line arguments; unused
     */
    public static void main(String[] args) {
        Department department = new Department("Engineering", new Manager("Ada Lovelace", 160000, 5));
        department.addDeveloper(new Developer("Mary-Jane O'Connor", 125000, "Platform"),
                OrdinalEnum.FIRST, StringEnum.HAPPY);
        department.addDeveloper(new Developer("Grace Hopper", 140000, "Compilers"),
                OrdinalEnum.SECOND, StringEnum.CHILL);

        System.out.println("Department: " + department.getName());
        System.out.println("Manager: " + department.getManager());
        department.getDeveloperAssignments().forEach(assignment -> System.out.printf(
                "Developer: %s, position: %s, happiness: %s (%s)%n",
                assignment.developer().getName(), assignment.position(),
                assignment.happiness(), assignment.happiness().getValue()));
        System.out.println("Annual payroll: " + department.getAnnualPayroll());
    }
}