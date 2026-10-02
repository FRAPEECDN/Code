package com.frapee;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test.
     */
    @Test
    void testApp() {
        assertEquals(1, 1);
    }

    @Test
    void testMenuSelection() {
        App app = new App();
        assertEquals(false, app.runSelection(0));
        assertEquals(true, app.runSelection(1));
        assertEquals(true, app.runSelection(19));
    }
}
