package com.fp.coding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NameValidationTest {
    @Test
    void allowsCommonPunctuationAcrossInformationTypes() {
        String input = "  Mary-Jane O'Connor Jr.  ";
        String expected = "Mary-Jane O'Connor Jr.";

        assertEquals(expected, new PojoInformation(1, input, 30).getName());
        assertEquals(expected, new RecordInformation(input, 30, 0).name());
        assertEquals(expected, new Manager(input, 100, 2).getName());
    }

    @Test
    void normalizesUnicodeNamesToCanonicalComposition() {
        String decomposedName = "Jose\u0301 O\u2019Connor";
        String normalizedName = "Jos\u00E9 O\u2019Connor";

        assertEquals(normalizedName, new PojoInformation(1, decomposedName, 30).getName());
        assertEquals(normalizedName, new RecordInformation(decomposedName, 30, 0).name());
        assertEquals(normalizedName, new Developer(decomposedName, 100, "Testing").getName());
    }

    @Test
    void rejectsNamesWithoutLettersOrWithUnsupportedCharacters() {
        assertThrows(IllegalArgumentException.class, () -> new PojoInformation(1, "123", 30));
        assertThrows(IllegalArgumentException.class, () -> new RecordInformation("***", 30, 0));
        assertThrows(IllegalArgumentException.class, () -> new Manager("Alice/Smith", 100, 2));
        assertThrows(IllegalArgumentException.class, () -> new PojoInformation(1, null, 30));
        assertThrows(IllegalArgumentException.class, () -> new RecordInformation("  ", 30, 0));
    }

    @Test
    void allowsSupportedPunctuationAndUnicodeMarkCategories() {
        assertEquals("Mary-Jane O'Connor Jr.",
                new RecordInformation("Mary-Jane O'Connor Jr.", 30, 0).name());
        assertAllowedName(new int[] { 'A', 0x2018, 'B' });
        assertAllowedName(new int[] { 'A', 0x2019, 'B' });
        assertAllowedName(new int[] { 'A', 0x2010, 'B' });
        assertAllowedName(new int[] { 'A', 0x2011, 'B' });
        assertAllowedName(new int[] { 'A', 0x1AB0 });
        assertAllowedName(new int[] { 0x0915, 0x093E });
        assertAllowedName(new int[] { 'A', 0x20DD });
    }

    private static void assertAllowedName(int[] codePoints) {
        String name = new String(codePoints, 0, codePoints.length);
        assertEquals(name, new RecordInformation(name, 30, 0).name());
    }
}