package com.example.reporting.service;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import com.example.reporting.service.replacer.InsertPlaceholderReplacer;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertPlaceholderReplacerTest {

    private InsertPlaceholderReplacer replacer;
    private XMLSlideShow presentation;

    @BeforeEach
    void setUp() {
        replacer = new InsertPlaceholderReplacer();
        presentation = new XMLSlideShow();
    }

    @Test
    void canHandle_shouldReturnTrueForInsertPlaceholder() {
        Placeholder placeholder = Placeholder.builder()
            .key("price")
            .type(PlaceholderType.INSERT)
            .value("1000")
            .build();

        assertTrue(replacer.canHandle(placeholder));
    }

    @Test
    void canHandle_shouldReturnFalseForTextPlaceholder() {
        Placeholder placeholder = Placeholder.builder()
            .key("price")
            .type(PlaceholderType.TEXT)
            .value("1000")
            .build();

        assertFalse(replacer.canHandle(placeholder));
    }

    @Test
    void replace_shouldInsertValueIntoExistingText() {
        XSLFSlide slide = presentation.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();
        textRun.setText("Цена: $цена руб.");

        Placeholder placeholder = Placeholder.builder()
            .key("цена")
            .type(PlaceholderType.INSERT)
            .value("1000")
            .build();

        replacer.replace(presentation, placeholder);

        String resultText = textBox.getText();
        assertEquals("Цена: 1000 руб.", resultText);
    }

    @Test
    void replace_shouldHandleMultipleInsertsInSameText() {
        XSLFSlide slide = presentation.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();
        textRun.setText("Продукт: $продукт, Цена: $цена, Количество: $количество");

        Placeholder placeholder1 = Placeholder.builder()
            .key("продукт")
            .type(PlaceholderType.INSERT)
            .value("Ноутбук")
            .build();

        Placeholder placeholder2 = Placeholder.builder()
            .key("цена")
            .type(PlaceholderType.INSERT)
            .value("50000")
            .build();

        Placeholder placeholder3 = Placeholder.builder()
            .key("количество")
            .type(PlaceholderType.INSERT)
            .value("2")
            .build();

        replacer.replace(presentation, placeholder1);
        replacer.replace(presentation, placeholder2);
        replacer.replace(presentation, placeholder3);

        String resultText = textBox.getText();
        assertEquals("Продукт: Ноутбук, Цена: 50000, Количество: 2", resultText);
    }

    @Test
    void replace_shouldPreserveTextWithoutPlaceholder() {
        XSLFSlide slide = presentation.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();
        textRun.setText("Статический текст без плейсхолдеров");

        Placeholder placeholder = Placeholder.builder()
            .key("несуществующий")
            .type(PlaceholderType.INSERT)
            .value("значение")
            .build();

        replacer.replace(presentation, placeholder);

        String resultText = textBox.getText();
        assertEquals("Статический текст без плейсхолдеров", resultText);
    }

    @Test
    void replace_shouldInsertValueInSentence() {
        XSLFSlide slide = presentation.createSlide();
        XSLFTextBox textBox = slide.createTextBox();
        XSLFTextParagraph paragraph = textBox.addNewTextParagraph();
        XSLFTextRun textRun = paragraph.addNewTextRun();
        textRun.setText("Компания $компания достигла выручки $выручка за $период.");

        Placeholder[] placeholders = {
            Placeholder.builder()
                .key("компания")
                .type(PlaceholderType.INSERT)
                .value("ООО Рога и Копыта")
                .build(),
            Placeholder.builder()
                .key("выручка")
                .type(PlaceholderType.INSERT)
                .value("15 млн руб.")
                .build(),
            Placeholder.builder()
                .key("период")
                .type(PlaceholderType.INSERT)
                .value("Q4 2024")
                .build()
        };

        for (Placeholder placeholder : placeholders) {
            replacer.replace(presentation, placeholder);
        }

        String resultText = textBox.getText();
        assertEquals("Компания ООО Рога и Копыта достигла выручки 15 млн руб. за Q4 2024.", resultText);
    }
}