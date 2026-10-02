package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

class FoundationalExamplesTest {
    @Test
    void enumsExposeExpectedValuesAndSafeLookup() {
        assertEquals("Manager", AdvanceEnum.MANAGER.getRole());
        assertEquals(1, AdvanceEnum.MANAGER.getLevel());
        assertEquals(120000.0, AdvanceEnum.MANAGER.getSalary());
        assertEquals("Developer", AdvanceEnum.DEVELOPER.getRole());
        assertEquals(2, AdvanceEnum.DEVELOPER.getLevel());
        assertEquals(80000.0, AdvanceEnum.DEVELOPER.getSalary());
        assertEquals("Tester", AdvanceEnum.TESTER.getRole());
        assertEquals(3, AdvanceEnum.TESTER.getLevel());
        assertEquals(60000.0, AdvanceEnum.TESTER.getSalary());
        assertEquals("Designer", AdvanceEnum.DESIGNER.getRole());
        assertEquals(4, AdvanceEnum.DESIGNER.getLevel());
        assertEquals(90000.0, AdvanceEnum.DESIGNER.getSalary());

        assertArrayEquals(new OrdinalEnum[] {
                OrdinalEnum.FIRST, OrdinalEnum.SECOND, OrdinalEnum.THIRD, OrdinalEnum.FOURTH, OrdinalEnum.FIFTH
        }, OrdinalEnum.values());
        assertEquals(2, OrdinalEnum.THIRD.ordinal());

        assertEquals(StringEnum.HAPPY, StringEnum.fromValue("HAPPY"));
        assertEquals(StringEnum.CHILL, StringEnum.fromValue("CHILL"));
        assertEquals("happy", StringEnum.HAPPY.getValue());
        assertEquals("sad", StringEnum.SAD.getValue());
        assertEquals("mad", StringEnum.MAD.getValue());
        assertEquals("ok", StringEnum.OK.getValue());
        assertEquals("chill", StringEnum.CHILL.getValue());
        assertThrows(IllegalArgumentException.class, () -> StringEnum.fromValue("unknown"));
        assertThrows(IllegalArgumentException.class, () -> StringEnum.fromValue(null));
    }

    @Test
    void pojoConstructorsAccessorsCopyAndEqualityBehaveConsistently() {
        PojoInformation empty = new PojoInformation();
        assertEquals(0, empty.getId());
        assertEquals("Unknown", empty.getName());
        assertEquals(1, empty.getAge());
        assertEquals(null, empty.getSalary());
        assertFalse(empty.isActive());
        assertTrue(empty.getCreatedDate() != null);
        assertTrue(empty.getSnapShot() != null);

        PojoInformation pojo = new PojoInformation(7, "Ada Lovelace", 36, 120000.0);
        LocalDate createdDate = LocalDate.of(2020, 1, 2);
        LocalDateTime snapshot = LocalDateTime.of(2020, 1, 2, 3, 4);
        pojo.setId(8);
        pojo.setName("  José O'Connor  ");
        pojo.setAge(37);
        pojo.setSalary(null);
        pojo.setActive(true);
        pojo.setCreatedDate(createdDate);
        pojo.setSnapShot(snapshot);

        assertEquals(8, pojo.getId());
        assertEquals("José O'Connor", pojo.getName());
        assertEquals(37, pojo.getAge());
        assertEquals(null, pojo.getSalary());
        assertTrue(pojo.isActive());
        assertEquals(createdDate, pojo.getCreatedDate());
        assertEquals(snapshot, pojo.getSnapShot());
        assertTrue(pojo.summary().contains("José O'Connor"));
        assertTrue(pojo.toString().contains("active=true"));

        PojoInformation copy = pojo.copy();
        assertNotSame(pojo, copy);
        assertEquals(pojo, copy);
        assertEquals(pojo.hashCode(), copy.hashCode());
        assertEquals(pojo, pojo.clone());
        assertTrue(pojo.equals(pojo));
        assertFalse(pojo.equals(null));
        assertFalse(pojo.equals("not a POJO"));

        assertPojoDifferent(pojo, changed -> changed.setId(9));
        assertPojoDifferent(pojo, changed -> changed.setName("Grace Hopper"));
        assertPojoDifferent(pojo, changed -> changed.setAge(38));
        assertPojoDifferent(pojo, changed -> changed.setSalary(0.0));
        assertPojoDifferent(pojo, changed -> changed.setActive(false));
        assertPojoDifferent(pojo, changed -> changed.setCreatedDate(createdDate.plusDays(1)));
        assertPojoDifferent(pojo, changed -> changed.setSnapShot(snapshot.plusSeconds(1)));
    }

    @Test
    void pojoAndRecordRejectInvalidValuesAndRecordHasValueSemantics() {
        assertThrows(IllegalArgumentException.class, () -> new PojoInformation(1, "Ada", 0));
        PojoInformation pojo = new PojoInformation(1, "Ada", 30);
        assertThrows(IllegalArgumentException.class, () -> pojo.setAge(-1));
        assertThrows(IllegalArgumentException.class, () -> pojo.setSalary(-1.0));
        assertThrows(IllegalArgumentException.class, () -> pojo.setSalary(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> pojo.setSalary(Double.POSITIVE_INFINITY));
        assertThrows(NullPointerException.class, () -> pojo.setCreatedDate(null));
        assertThrows(NullPointerException.class, () -> pojo.setSnapShot(null));

        RecordInformation record = new RecordInformation("  Ada Lovelace  ", 36, 120000.0);
        assertEquals("Ada Lovelace", record.name());
        assertEquals(36, record.age());
        assertEquals(120000.0, record.salary());
        assertEquals(record, new RecordInformation("Ada Lovelace", 36, 120000.0));
        assertEquals(record.hashCode(), new RecordInformation("Ada Lovelace", 36, 120000.0).hashCode());
        assertNotEquals(record, new RecordInformation("Ada Lovelace", 37, 120000.0));
        assertTrue(record.summary().contains("Ada Lovelace"));

        assertThrows(IllegalArgumentException.class, () -> new RecordInformation(null, 36, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new RecordInformation(" ", 36, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new RecordInformation("Ada", 0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new RecordInformation("Ada", 36, -1.0));
        assertThrows(IllegalArgumentException.class, () -> new RecordInformation("Ada", 36, Double.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> new RecordInformation("Ada", 36, Double.POSITIVE_INFINITY));
    }

    @Test
    void staffEqualityIncludesConcreteStateAndValidation() {
        Manager manager = new Manager("Ada Lovelace", 160000.0, 5);
        Manager sameManager = new Manager("Ada Lovelace", 160000.0, 5);
        assertEquals(manager, sameManager);
        assertTrue(manager.equals(manager));
        assertEquals(manager.hashCode(), sameManager.hashCode());
        assertNotEquals(manager, new Manager("Ada Lovelace", 160000.0, 6));
        assertNotEquals(manager, new Manager("Grace Hopper", 160000.0, 5));
        assertNotEquals(manager, new Manager("Ada Lovelace", 150000.0, 5));
        assertEquals("Manager", manager.roleTitle());
        assertEquals("Ada Lovelace", manager.getName());
        assertEquals(5, manager.getTeamSize());
        assertTrue(manager.summary().contains("Manager"));
        assertTrue(manager.toString().contains("teamSize=5"));

        Developer developer = new Developer("Grace Hopper", 140000.0, "Compilers");
        Developer sameDeveloper = new Developer("Grace Hopper", 140000.0, "Compilers");
        assertEquals(developer, sameDeveloper);
        assertTrue(developer.equals(developer));
        assertEquals(developer.hashCode(), sameDeveloper.hashCode());
        assertNotEquals(developer, new Developer("Grace Hopper", 140000.0, "Navy"));
        assertNotEquals(developer, new Developer("Ada Lovelace", 140000.0, "Compilers"));
        assertNotEquals(developer, new Developer("Grace Hopper", 130000.0, "Compilers"));
        assertNotEquals(manager, developer);
        assertEquals("Developer", developer.roleTitle());
        assertEquals("Compilers", developer.getSpecialty());
        assertTrue(developer.summary().contains("Developer"));
        assertTrue(developer.toString().contains("specialty='Compilers'"));

        assertThrows(IllegalArgumentException.class, () -> new Manager("Ada", -1.0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Manager("Ada", 1.0, -1));
        assertThrows(IllegalArgumentException.class, () -> new Developer("Ada", 1.0, " "));
        assertThrows(IllegalArgumentException.class, () -> new Developer("Ada", 1.0, null));
        assertThrows(IllegalArgumentException.class, () -> new Developer("Ada", Double.NaN, "Testing"));

        class EqualityProbe extends Developer {
            EqualityProbe() {
                super("Probe Person", 100, "Testing");
            }

            boolean staffEqualityWith(Object other) {
                return super.equals(other);
            }
        }
        EqualityProbe probe = new EqualityProbe();
        assertTrue(probe.staffEqualityWith(probe));
        assertFalse(probe.staffEqualityWith(null));
        assertFalse(probe.staffEqualityWith(new Manager("Probe Person", 100, 0)));
        assertTrue(probe.staffEqualityWith(new EqualityProbe()));
    }

    @Test
    void catalogAndGenericShowcaseCoverOptionalAndWildcardPaths() throws Exception {
        InformationCatalog catalog = new InformationCatalog();
        assertTrue(catalog.entries().isEmpty());
        assertEquals(Optional.empty(), catalog.findPojoById(404));
        assertEquals(Optional.empty(), catalog.findPojoSalary(404));
        assertThrows(NullPointerException.class, () -> catalog.add((PojoInformation) null));
        assertThrows(NullPointerException.class, () -> catalog.add((RecordInformation) null));
        assertThrows(NullPointerException.class, () -> catalog.findRecordByName(null));

        PojoInformation pojo = new PojoInformation(1, "Ada Lovelace", 36, 120000.0);
        PojoInformation noSalary = new PojoInformation(2, "Katherine Johnson", 26);
        RecordInformation record = new RecordInformation("Grace Hopper", 85, 150000.0);
        catalog.add(pojo);
        catalog.add(noSalary);
        catalog.add(record);
        assertTrue(catalog.findPojoById(1).isPresent());
        assertEquals(Optional.of(120000.0), catalog.findPojoSalary(1));
        assertEquals(Optional.empty(), catalog.findPojoSalary(2));
        assertTrue(catalog.findRecordByName("Grace Hopper").isPresent());
        assertTrue(catalog.findRecordByName("Unknown").isEmpty());
        assertEquals(3, catalog.entries().size());
        assertThrows(UnsupportedOperationException.class, () -> catalog.entries().clear());

        GenericShowcase<Information> showcase = new GenericShowcase<>();
        showcase.add(pojo);
        showcase.addAll(List.of(record));
        assertEquals(2, showcase.items().size());
        assertThrows(UnsupportedOperationException.class, () -> showcase.items().clear());
        ArrayList<Object> destination = new ArrayList<>();
        showcase.copyTo(destination);
        assertEquals(2, destination.size());
        assertThrows(NullPointerException.class, () -> showcase.add(null));
        assertThrows(NullPointerException.class, () -> showcase.addAll(null));
        assertThrows(NullPointerException.class, () -> showcase.copyTo(null));
        assertEquals(9, GenericShowcase.largest(List.of(3, 9, 4)));
        assertThrows(IllegalArgumentException.class, () -> GenericShowcase.largest(List.of()));

        List<Information> entries = List.of(pojo, record);
        String printed = captureOutput(() -> {
            showcase.display("Showcase");
            GenericShowcase.displayWildcard("Wildcard", List.of("value"));
            GenericShowcase.displaySummaries(entries);
        });
        assertTrue(printed.contains("Wildcard"));
        assertTrue(printed.contains("Ada Lovelace"));
        assertTrue(printed.contains("Grace Hopper"));
    }

    @Test
    void runnableExamplesProduceExpectedOutput() throws Exception {
        String genericOutput = captureOutput(() -> GenericExamples.main(new String[0]));
        assertTrue(genericOutput.contains("All information through GenericShowcase"));
        assertTrue(genericOutput.contains("UuidPojoInformation"));

        String objectsOutput = captureOutput(() -> ObjectsOptionalExamples.main(new String[0]));
        assertTrue(objectsOutput.contains("Objects.equals"));
        assertTrue(objectsOutput.contains("No matching record"));
        assertTrue(objectsOutput.contains("No matching POJO"));

        String departmentOutput = captureOutput(() -> DepartmentExamples.main(new String[0]));
        assertTrue(departmentOutput.contains("position: FIRST"));
        assertTrue(departmentOutput.contains("happiness: HAPPY"));

        String uuidOutput = captureOutput(() -> UuidPojoExamples.main(new String[0]));
        assertTrue(uuidOutput.contains("Natural Comparable order"));
        assertTrue(uuidOutput.contains("Custom Comparator order by age"));
        assertTrue(uuidOutput.contains("Serializable round trip preserves value: true"));
    }

    @Test
    void menuRunsSelectedSectionAndHandlesInvalidInputAndExit() throws Exception {
        ByteArrayOutputStream menuBytes = new ByteArrayOutputStream();
        String sectionOutput = captureOutput(() -> ExampleMenu.runMenu(
                new Scanner("invalid\n2\n0\n"), new PrintStream(menuBytes, true, StandardCharsets.UTF_8)));

        String menuOutput = menuBytes.toString(StandardCharsets.UTF_8);
        assertTrue(menuOutput.contains("Choose a section from 1 to 9"));
        assertTrue(menuOutput.contains("--- Enums ---"));
        assertTrue(menuOutput.contains("Exiting examples."));
        assertTrue(sectionOutput.contains("Data-bearing role enum"));
        assertFalse(sectionOutput.contains("POJO model:"));
    }

    @Test
    void menuRunAllExecutesEveryRunnableSection() throws Exception {
        ByteArrayOutputStream menuBytes = new ByteArrayOutputStream();
        String examplesOutput = captureOutput(() -> ExampleMenu.runMenu(
                new Scanner("10\n0\n"), new PrintStream(menuBytes, true, StandardCharsets.UTF_8)));

        String menuOutput = menuBytes.toString(StandardCharsets.UTF_8);
        assertTrue(menuOutput.contains("--- POJO and record models ---"));
        assertTrue(menuOutput.contains("--- Enums ---"));
        assertTrue(menuOutput.contains("--- Inheritance and polymorphism ---"));
        assertTrue(menuOutput.contains("--- Generics and Java 21 language features ---"));
        assertTrue(menuOutput.contains("--- Objects, Optional, and catalog composition ---"));
        assertTrue(menuOutput.contains("--- Department composition ---"));
        assertTrue(menuOutput.contains("--- UUID, Comparable, Comparator, and serialization ---"));
        assertTrue(menuOutput.contains("--- List, Set, Map, searching, and streams ---"));
        assertTrue(menuOutput.contains("--- Java 21 sequenced collections ---"));
        assertTrue(examplesOutput.contains("POJO model:"));
        assertTrue(examplesOutput.contains("Data-bearing role enum:"));
        assertTrue(examplesOutput.contains("Manager{"));
        assertTrue(examplesOutput.contains("All information through GenericShowcase"));
        assertTrue(examplesOutput.contains("Objects.equals"));
        assertTrue(examplesOutput.contains("Department: Engineering"));
        assertTrue(examplesOutput.contains("Serializable round trip preserves value: true"));
        assertTrue(examplesOutput.contains("Stream operations over Map values"));
        assertTrue(examplesOutput.contains("Sequenced Map first/last entry"));
    }

    @Test
    void uuidPojoAccessorsEqualityAndValidationAreCovered() {
        UuidPojoInformation generated = new UuidPojoInformation();
        assertTrue(generated.getId() != null);
        assertEquals("Unknown", generated.getName());
        assertEquals(1, generated.getAge());
        assertEquals(null, generated.getSalary());

        UUID id = UUID.randomUUID();
        UuidPojoInformation pojo = new UuidPojoInformation(id, "Ada Lovelace", 36, 120000.0);
        pojo.setName("Grace Hopper");
        pojo.setAge(37);
        pojo.setSalary(null);
        assertEquals(id, pojo.getId());
        assertEquals("Grace Hopper", pojo.getName());
        assertEquals(37, pojo.getAge());
        assertEquals(null, pojo.getSalary());
        assertTrue(pojo.summary().contains(id.toString()));
        assertEquals(pojo.summary(), pojo.toString());
        assertTrue(pojo.equals(pojo));
        assertFalse(pojo.equals(null));
        assertFalse(pojo.equals("not a UUID POJO"));
        assertNotEquals(pojo, new UuidPojoInformation(id, "Grace Hopper", 38, null));
        assertThrows(NullPointerException.class, () -> pojo.compareTo(null));
        assertThrows(NullPointerException.class, () -> new UuidPojoInformation(null, "Ada", 30, null));
        assertThrows(IllegalArgumentException.class, () -> pojo.setAge(0));
        assertThrows(IllegalArgumentException.class, () -> pojo.setSalary(-1.0));
        assertThrows(IllegalArgumentException.class, () -> pojo.setSalary(Double.POSITIVE_INFINITY));

        UuidPojoInformation baseline = new UuidPojoInformation(id, "Grace Hopper", 37, null);
        assertNotEquals(baseline, new UuidPojoInformation(UUID.randomUUID(), "Grace Hopper", 37, null));
        assertNotEquals(baseline, new UuidPojoInformation(id, "Ada Lovelace", 37, null));
        assertNotEquals(baseline, new UuidPojoInformation(id, "Grace Hopper", 38, null));
        assertNotEquals(baseline, new UuidPojoInformation(id, "Grace Hopper", 37, 0.0));
        assertEquals(baseline.hashCode(), new UuidPojoInformation(id, "Grace Hopper", 37, null).hashCode());
        assertTrue(baseline.compareTo(new UuidPojoInformation(id, "Grace Hopper", 37, 0.0)) < 0);
        assertTrue(baseline.compareTo(new UuidPojoInformation(id, "Grace Hopper", 37, null)) == 0);
        UuidPojoInformation sameAgeByName = new UuidPojoInformation(UUID.randomUUID(), "Zoe", 37, null);
        UuidPojoInformation sameNameOtherAge = new UuidPojoInformation(UUID.randomUUID(), "Grace Hopper", 38, null);
        assertTrue(UuidPojoInformation.BY_AGE.compare(baseline, sameAgeByName) < 0);
        assertTrue(UuidPojoInformation.BY_AGE.compare(baseline, sameNameOtherAge) < 0);
    }

    private static void assertPojoDifferent(PojoInformation original, Consumer<PojoInformation> update) {
        PojoInformation changed = original.copy();
        update.accept(changed);
        assertNotEquals(original, changed);
    }

    private static String captureOutput(OutputAction action) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream capture = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(capture);
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @FunctionalInterface
    private interface OutputAction {
        void run() throws Exception;
    }
}