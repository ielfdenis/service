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

/**
 * Сервис для генерации месячных отчетов
 * Шаблон: templates/monthly-report.pptx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MonthlyReportService {

    private static final String TEMPLATE_PATH = "templates/monthly-report.pptx";

    private final PptxModificationService modificationService;
    private final PptxDownloadService downloadService;

    /**
     * Генерирует месячный отчет
     */
    public ResponseEntity<Resource> generateMonthlyReport(
        String month,
        String year,
        String companyName,
        String revenue,
        String expenses,
        String profit,
        Path logoPath,
        Path revenueChartPath,
        Path expensesChartPath
    ) throws Exception {

        log.info("Generating monthly report for {} {}, company: {}", month, year, companyName);

        List<Placeholder> placeholders = new ArrayList<>();

        // Заголовок отчета (полная замена)
        placeholders.add(Placeholder.builder()
            .key("reportTitle")
            .type(PlaceholderType.TEXT)
            .value(String.format("Финансовый отчет за %s %s", month, year))
            .build());

        // Вставка данных компании
        placeholders.add(Placeholder.builder()
            .key("companyName")
            .type(PlaceholderType.INSERT)
            .value(companyName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("месяц")
            .type(PlaceholderType.INSERT)
            .value(month)
            .build());

        placeholders.add(Placeholder.builder()
            .key("год")
            .type(PlaceholderType.INSERT)
            .value(year)
            .build());

        // Финансовые данные (вставка в текст "Выручка: $revenue")
        placeholders.add(Placeholder.builder()
            .key("выручка")
            .type(PlaceholderType.INSERT)
            .value(revenue)
            .build());

        placeholders.add(Placeholder.builder()
            .key("расходы")
            .type(PlaceholderType.INSERT)
            .value(expenses)
            .build());

        placeholders.add(Placeholder.builder()
            .key("прибыль")
            .type(PlaceholderType.INSERT)
            .value(profit)
            .build());

        // Логотип компании
        if (logoPath != null) {
            ImageData logo = ImageUtils.loadImageFromFile(logoPath);
            placeholders.add(Placeholder.builder()
                .key("logo")
                .type(PlaceholderType.IMAGE)
                .value(logo)
                .build());
        }

        // График выручки
        if (revenueChartPath != null) {
            ImageData revenueChart = ImageUtils.loadImageFromFile(revenueChartPath);
            placeholders.add(Placeholder.builder()
                .key("revenueChart")
                .type(PlaceholderType.IMAGE)
                .value(revenueChart)
                .build());
        }

        // График расходов
        if (expensesChartPath != null) {
            ImageData expensesChart = ImageUtils.loadImageFromFile(expensesChartPath);
            placeholders.add(Placeholder.builder()
                .key("expensesChart")
                .type(PlaceholderType.IMAGE)
                .value(expensesChart)
                .build());
        }

        TemplateData templateData = TemplateData.builder()
            .templateName(TEMPLATE_PATH)
            .placeholders(placeholders)
            .build();

        XMLSlideShow presentation = modificationService.modifyPresentation(templateData);
        String filename = String.format("monthly-report-%s-%s.pptx", month, year);

        ResponseEntity<Resource> response = downloadService.prepareDownload(presentation, filename);
        presentation.close();

        log.info("Successfully generated monthly report for {} {}", month, year);
        return response;
    }
}