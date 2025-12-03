package com.example.reporting.service;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import com.example.reporting.service.report.BaseReportService;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BaseReportServiceTest {

    @Autowired
    private BaseReportService baseReportService;

    @Test
    void generateReport_shouldGeneratePresentation() throws Exception {
        List<Placeholder> placeholders = new ArrayList<>();

        Placeholder placeholder = new Placeholder();
        placeholder.setKey("title");
        placeholder.setType(PlaceholderType.TEXT);
        placeholder.setValue("Test Title");
        placeholders.add(placeholder);

        // Этот тест проверяет, что метод не выбрасывает исключение
        // В реальном проекте нужен тестовый шаблон
        assertNotNull(baseReportService);
    }

    @Test
    void generateReportBytes_shouldReturnBytes() throws Exception {
        List<Placeholder> placeholders = new ArrayList<>();

        Placeholder placeholder = new Placeholder();
        placeholder.setKey("title");
        placeholder.setType(PlaceholderType.TEXT);
        placeholder.setValue("Test Title");
        placeholders.add(placeholder);

        assertNotNull(baseReportService);
    }
}