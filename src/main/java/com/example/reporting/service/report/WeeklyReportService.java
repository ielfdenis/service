package com.example.reporting.service.report;

import com.example.reporting.model.ImageData;
import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import com.example.reporting.model.TemplateData;
import com.example.reporting.service.PptxDownloadService;
import com.example.reporting.service.PptxModificationService;
import com.example.reporting.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Сервис для генерации еженедельных отчетов
 * Шаблон: templates/weekly-report.pptx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class
WeeklyReportService {

    private static final String TEMPLATE_PATH = "templates/weekly-report.pptx";

    private final BaseReportService baseReportService;

    /**
     * Генерирует еженедельный отчет
     *
     * @param weekNumber номер недели
     * @param projectName название проекта
     * @param tasksCompleted количество выполненных задач
     * @param tasksInProgress количество задач в работе
     * @param chartImagePath путь к изображению графика
     * @return ResponseEntity с PPTX файлом
     */
    public ResponseEntity<Resource> generateWeeklyReport(
        int weekNumber,
        String projectName,
        int tasksCompleted,
        int tasksInProgress,
        Path chartImagePath
    ) throws Exception {

        log.info("Generating weekly report for week {}, project: {}", weekNumber, projectName);

        List<Placeholder> placeholders = new ArrayList<>();

        // Заголовок (полная замена)
        placeholders.add(Placeholder.builder()
            .key("reportTitle")
            .type(PlaceholderType.TEXT)
            .value("Еженедельный отчет - Неделя " + weekNumber)
            .build());

        // Вставка значений в существующий текст
        placeholders.add(Placeholder.builder()
            .key("projectName")
            .type(PlaceholderType.INSERT)
            .value(projectName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("weekNumber")
            .type(PlaceholderType.INSERT)
            .value(String.valueOf(weekNumber))
            .build());

        placeholders.add(Placeholder.builder()
            .key("tasksCompleted")
            .type(PlaceholderType.INSERT)
            .value(String.valueOf(tasksCompleted))
            .build());

        placeholders.add(Placeholder.builder()
            .key("tasksInProgress")
            .type(PlaceholderType.INSERT)
            .value(String.valueOf(tasksInProgress))
            .build());

        // Изображение графика
        if (chartImagePath != null) {
            ImageData chartImage = ImageUtils.loadImageFromFile(chartImagePath);
            placeholders.add(Placeholder.builder()
                .key("chart")
                .type(PlaceholderType.IMAGE)
                .value(chartImage)
                .build());
        }

        // Используем базовый метод для генерации
        String filename = String.format("weekly-report-week-%d.pptx", weekNumber);
        return baseReportService.generateReport(TEMPLATE_PATH, placeholders, filename);
    }

    /**
     * Упрощенная версия с Map параметров
     */
    public ResponseEntity<Resource> generateWeeklyReport(Map<String, Object> data) throws Exception {
        int weekNumber = (int) data.getOrDefault("weekNumber", 1);
        String projectName = (String) data.getOrDefault("projectName", "Unknown Project");
        int tasksCompleted = (int) data.getOrDefault("tasksCompleted", 0);
        int tasksInProgress = (int) data.getOrDefault("tasksInProgress", 0);
        Path chartImagePath = (Path) data.get("chartImagePath");

        return generateWeeklyReport(weekNumber, projectName, tasksCompleted, tasksInProgress, chartImagePath);
    }
}