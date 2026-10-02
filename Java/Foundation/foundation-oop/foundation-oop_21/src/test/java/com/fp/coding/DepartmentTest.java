package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class DepartmentTest {
    @Test
    void holdsOneManagerAndMultipleRatedDevelopers() {
        Manager manager = new Manager("Ada Lovelace", 160000, 5);
        Developer first = new Developer("Mary-Jane O'Connor", 125000, "Platform");
        Developer second = new Developer("Grace Hopper", 140000, "Compilers");
        Department department = new Department(" Engineering ", manager);

        department.addDeveloper(first, OrdinalEnum.FIRST, StringEnum.HAPPY);
        department.addDeveloper(second, OrdinalEnum.SECOND, StringEnum.CHILL);

        assertEquals("Engineering", department.getName());
        assertSame(manager, department.getManager());
        assertEquals(2, department.getDeveloperAssignments().size());
        assertSame(first, department.getDeveloperAssignments().get(0).developer());
        assertEquals(OrdinalEnum.FIRST, department.getDeveloperAssignments().get(0).position());
        assertEquals(StringEnum.HAPPY, department.getDeveloperAssignments().get(0).happiness());
        assertEquals(3, department.getStaffMembers().size());
        assertEquals(425000.0, department.getAnnualPayroll(), 0.001);
        assertThrows(UnsupportedOperationException.class, () -> department.getDeveloperAssignments().clear());
        assertThrows(UnsupportedOperationException.class, () -> department.getStaffMembers().clear());
    }

    @Test
    void requiresAManagerAndNonBlankDepartmentName() {
        assertThrows(NullPointerException.class, () -> new Department("Engineering", null));
        assertThrows(IllegalArgumentException.class,
            () -> new Department(null, new Manager("Ada Lovelace", 160000, 5)));
        assertThrows(IllegalArgumentException.class,
                () -> new Department(" ", new Manager("Ada Lovelace", 160000, 5)));
    }

        @Test
        void requiresCompleteDeveloperAssignments() {
        Department department = new Department("Engineering", new Manager("Ada Lovelace", 160000, 5));
        Developer developer = new Developer("Grace Hopper", 140000, "Compilers");

        assertThrows(NullPointerException.class,
            () -> department.addDeveloper(null, OrdinalEnum.FIRST, StringEnum.HAPPY));
        assertThrows(NullPointerException.class,
            () -> department.addDeveloper(developer, null, StringEnum.HAPPY));
        assertThrows(NullPointerException.class,
            () -> department.addDeveloper(developer, OrdinalEnum.FIRST, null));
        assertThrows(NullPointerException.class,
            () -> new Department.DeveloperAssignment(null, OrdinalEnum.FIRST, StringEnum.HAPPY));
        assertThrows(NullPointerException.class,
            () -> new Department.DeveloperAssignment(developer, null, StringEnum.HAPPY));
        assertThrows(NullPointerException.class,
            () -> new Department.DeveloperAssignment(developer, OrdinalEnum.FIRST, null));
        }
}