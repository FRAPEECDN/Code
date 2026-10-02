package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UuidPojoInformationTest {
    @Test
    void supportsNaturalAndCustomOrderingAndCopyIdentity() {
        UuidPojoInformation ada = new UuidPojoInformation(UUID.randomUUID(), "Ada Lovelace", 36, 120000.0);
        UuidPojoInformation grace = new UuidPojoInformation(UUID.randomUUID(), "Grace Hopper", 85, 150000.0);

        assertEquals(List.of("Ada Lovelace", "Grace Hopper"), List.of(ada, grace).stream()
                .sorted().map(UuidPojoInformation::getName).toList());
        assertEquals(List.of("Ada Lovelace", "Grace Hopper"), List.of(grace, ada).stream()
                .sorted(UuidPojoInformation.BY_AGE).map(UuidPojoInformation::getName).toList());
        assertEquals(ada, ada.clone());
        assertNotSame(ada, ada.clone());
        UuidPojoInformation deepCopy = ada.deepCopy();
        assertNotSame(ada, deepCopy);
        assertEquals(ada, deepCopy);
        assertEquals(ada.getId(), deepCopy.getId());
        deepCopy.setName("Ada Byron");
        deepCopy.setAge(37);
        deepCopy.setSalary(1.0);
        assertEquals("Ada Lovelace", ada.getName());
        assertEquals(36, ada.getAge());
        assertEquals(120000.0, ada.getSalary());
        assertNotEquals(ada, ada.copyWithNewId());
        assertEquals(ada.getName(), ada.copyWithNewId().getName());
        assertEquals(ada.getAge(), ada.copyWithNewId().getAge());
        assertEquals(ada.getSalary(), ada.copyWithNewId().getSalary());
        assertEquals(0, ada.compareTo(ada.clone()));
    }

    @Test
    void serializesAndRestoresAllBeanState() throws Exception {
        UuidPojoInformation original = new UuidPojoInformation("José O'Connor", 38, null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(original);
        }

        UuidPojoInformation restored;
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            restored = (UuidPojoInformation) input.readObject();
        }

        assertEquals(original, restored);
        assertEquals(original.getId(), restored.getId());
        assertTrue(restored.getSalary() == null);
    }
}