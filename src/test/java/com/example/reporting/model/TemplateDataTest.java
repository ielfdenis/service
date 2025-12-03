package com.example.reporting.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TemplateDataTest {

    @Test
    void templateData_shouldSetAndGetValues() {
        TemplateData templateData = new TemplateData();
        templateData.setTemplateName("test-template.pptx");

        List<Placeholder> placeholders = new ArrayList<>();
        Placeholder placeholder = new Placeholder();
        placeholder.setKey("key1");
        placeholder.setType(PlaceholderType.TEXT);
        placeholder.setValue("value1");
        placeholders.add(placeholder);

        templateData.setPlaceholders(placeholders);

        assertEquals("test-template.pptx", templateData.getTemplateName());
        assertEquals(1, templateData.getPlaceholders().size());
        assertEquals("key1", templateData.getPlaceholders().get(0).getKey());
    }

    @Test
    void templateData_shouldHandleMultiplePlaceholders() {
        TemplateData templateData = new TemplateData();

        List<Placeholder> placeholders = new ArrayList<>();

        Placeholder p1 = new Placeholder();
        p1.setKey("title");
        p1.setType(PlaceholderType.TEXT);
        p1.setValue("Title");
        placeholders.add(p1);

        Placeholder p2 = new Placeholder();
        p2.setKey("price");
        p2.setType(PlaceholderType.INSERT);
        p2.setValue("1000");
        placeholders.add(p2);

        templateData.setPlaceholders(placeholders);

        assertEquals(2, templateData.getPlaceholders().size());
    }

    @Test
    void templateData_shouldHandleEmptyPlaceholders() {
        TemplateData templateData = new TemplateData();
        templateData.setPlaceholders(new ArrayList<>());

        assertNotNull(templateData.getPlaceholders());
        assertEquals(0, templateData.getPlaceholders().size());
    }
}