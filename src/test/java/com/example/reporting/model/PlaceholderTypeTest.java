package com.example.reporting.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderTypeTest {

    @Test
    void placeholderType_shouldHaveThreeTypes() {
        PlaceholderType[] types = PlaceholderType.values();

        assertEquals(3, types.length);

        assertEquals(PlaceholderType.TEXT, types[0]);
        assertEquals(PlaceholderType.IMAGE, types[1]);
        assertEquals(PlaceholderType.INSERT, types[2]);
    }

    @Test
    void placeholderType_shouldBeComparable() {
        assertEquals(PlaceholderType.TEXT, PlaceholderType.valueOf("TEXT"));
        assertEquals(PlaceholderType.INSERT, PlaceholderType.valueOf("INSERT"));
        assertEquals(PlaceholderType.IMAGE, PlaceholderType.valueOf("IMAGE"));
    }

    @Test
    void placeholderType_shouldNotBeEqual() {
        assertNotEquals(PlaceholderType.TEXT, PlaceholderType.INSERT);
        assertNotEquals(PlaceholderType.INSERT, PlaceholderType.IMAGE);
        assertNotEquals(PlaceholderType.IMAGE, PlaceholderType.TEXT);
    }
}