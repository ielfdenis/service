package com.example.reporting.service.report;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.TemplateData;
import com.example.reporting.service.PptxDownloadService;
import com.example.reporting.service.PptxModificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Базовый сервис для всех типов отчетов
 * Предоставляет общие методы для работы с PPTX шаблонами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaseReportService {

    private final PptxModificationService modificationService;
    private final PptxDownloadService downloadService;

    /**
     * Универсальный метод генерации отчета
     * Может использоваться всеми специализированными сервисами
     *
     * @param templatePath путь к шаблону (например, "templates/weekly-report.pptx")
     * @param placeholders список плейсхолдеров с данными
     * @param outputFilename имя выходного файла
     * @return ResponseEntity с PPTX файлом для скачивания
     */
    public ResponseEntity<Resource> generateReport(
        String templatePath,
        List<Placeholder> placeholders,
        String outputFilename
    ) throws Exception {

        log.info("Generating report from template: {}, placeholders count: {}",
            templatePath, placeholders.size());

        TemplateData templateData = TemplateData.builder()
            .templateName(templatePath)
            .placeholders(placeholders)
            .build();

        XMLSlideShow presentation = modificationService.modifyPresentation(templateData);

        ResponseEntity<Resource> response = downloadService.prepareDownload(
            presentation,
            outputFilename
        );

        presentation.close();

        log.info("Successfully generated report: {}", outputFilename);
        return response;
    }

    /**
     * Универсальный метод для получения байтов презентации
     * Полезно, когда нужно сохранить файл, а не скачать
     */
    public byte[] generateReportBytes(
        String templatePath,
        List<Placeholder> placeholders
    ) throws Exception {

        log.info("Generating report bytes from template: {}", templatePath);

        TemplateData templateData = TemplateData.builder()
            .templateName(templatePath)
            .placeholders(placeholders)
            .build();

        XMLSlideShow presentation = modificationService.modifyPresentation(templateData);
        byte[] bytes = downloadService.convertToBytes(presentation);
        presentation.close();

        log.info("Successfully generated report bytes, size: {} bytes", bytes.length);
        return bytes;
    }

    /**
     * Универсальный метод для получения XMLSlideShow
     * Полезно, если нужна дополнительная обработка перед сохранением
     */
    public XMLSlideShow generateReportPresentation(
        String templatePath,
        List<Placeholder> placeholders
    ) throws Exception {

        log.info("Generating presentation from template: {}", templatePath);

        TemplateData templateData = TemplateData.builder()
            .templateName(templatePath)
            .placeholders(placeholders)
            .build();

        return modificationService.modifyPresentation(templateData);
    }
}