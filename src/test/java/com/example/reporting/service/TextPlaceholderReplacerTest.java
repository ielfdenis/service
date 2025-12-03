package com.example.reporting.service;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import com.example.reporting.service.replacer.TextPlaceholderReplacer;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextPlaceholderReplacerTest {

    private TextPlaceholderReplacer replacer;
    private XMLSlideShow presentation;

    @BeforeEach
    void setUp() {
        replacer = new TextPlaceholderReplacer();
        presentation = new XMLSlideShow();
    }

    @Test
    void canHandle_shouldReturnTrueForTextPlaceholder() {
        Placeholder placeholder = Placeholder.builder()
            .key("test")
            .type(PlaceholderType.TEXT)
            .value("value")
            .build();

        assertTrue(replacer.canHandle(placeholder));
    }

    @Test
    void canHandle_shouldReturnFalseForImagePlaceholder() {
        Placeholder placeholder = Placeholder.builder()
            .key("test")
            .type(PlaceholderType.IMAGE)
            .value("value")
            .build();

        assertFalse(replacer.canHandle(placeholder));
    }

    @Test
    void replace_shouldReplaceTextInPresentation() {
        XSLFSlide slide = presentation.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();
        textRun.setText("Hello {{name}}, welcome!");

        Placeholder placeholder = Placeholder.builder()
            .key("name")
            .type(PlaceholderType.TEXT)
            .value("World")
            .build();

        replacer.replace(presentation, placeholder);

        String resultText = textBox.getText();
        assertEquals("Hello World, welcome!", resultText);
    }

    @Test
    void replace_shouldHandleMultiplePlaceholdersInSameText() {
        XSLFSlide slide = presentation.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();
        textRun.setText("{{greeting}} {{name}}");

        Placeholder placeholder1 = Placeholder.builder()
            .key("greeting")
            .type(PlaceholderType.TEXT)
            .value("Hello")
            .build();

        replacer.replace(presentation, placeholder1);

        Placeholder placeholder2 = Placeholder.builder()
            .key("name")
            .type(PlaceholderType.TEXT)
            .value("John")
            .build();

        replacer.replace(presentation, placeholder2);

        String resultText = textBox.getText();
        assertEquals("Hello John", resultText);
    }
}