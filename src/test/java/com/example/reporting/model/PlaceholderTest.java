package com.example.reporting.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderTest {

    @Test
    void placeholder_shouldSetAndGetValues() {
        Placeholder placeholder = new Placeholder();
        placeholder.setKey("testKey");
        placeholder.setType(PlaceholderType.TEXT);
        placeholder.setValue("testValue");

        assertEquals("testKey", placeholder.getKey());
        assertEquals(PlaceholderType.TEXT, placeholder.getType());
        assertEquals("testValue", placeholder.getValue());
    }

    @Test
    void placeholder_shouldHandleInsertType() {
        Placeholder placeholder = new Placeholder();
        placeholder.setKey("price");
        placeholder.setType(PlaceholderType.INSERT);
        placeholder.setValue("1000");

        assertEquals(PlaceholderType.INSERT, placeholder.getType());
        assertEquals("price", placeholder.getKey());
    }

    @Test
    void placeholder_shouldHandleImageType() {
        Placeholder placeholder = new Placeholder();
        placeholder.setKey("logo");
        placeholder.setType(PlaceholderType.IMAGE);

        ImageData imageData = new ImageData();
        imageData.setImageBytes(new byte[]{1, 2, 3});
        placeholder.setValue(imageData);

        assertEquals(PlaceholderType.IMAGE, placeholder.getType());
        assertNotNull(placeholder.getValue());
        assertTrue(placeholder.getValue() instanceof ImageData);
    }

    @Test
    void placeholder_shouldHandleNullValues() {
        Placeholder placeholder = new Placeholder();

        assertNull(placeholder.getKey());
        assertNull(placeholder.getType());
        assertNull(placeholder.getValue());
    }
}