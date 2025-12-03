package com.example.reporting.service.report;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import com.example.reporting.model.TemplateData;
import com.example.reporting.service.PptxDownloadService;
import com.example.reporting.service.PptxModificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для генерации счетов/инвойсов
 * Шаблон: templates/invoice-template.pptx
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceReportService {

    private static final String TEMPLATE_PATH = "templates/invoice-template.pptx";

    private final PptxModificationService modificationService;
    private final PptxDownloadService downloadService;

    /**
     * Генерирует счет (invoice)
     */
    public ResponseEntity<Resource> generateInvoice(
        String invoiceNumber,
        String clientName,
        String clientAddress,
        String date,
        String productName,
        int quantity,
        double price,
        double total
    ) throws Exception {

        log.info("Generating invoice {} for client: {}", invoiceNumber, clientName);

        List<Placeholder> placeholders = new ArrayList<>();

        // Номер счета (полная замена)
        placeholders.add(Placeholder.builder()
            .key("invoiceNumber")
            .type(PlaceholderType.TEXT)
            .value(invoiceNumber)
            .build());

        // Данные клиента (вставка в шаблон вида "Клиент: $clientName")
        placeholders.add(Placeholder.builder()
            .key("clientName")
            .type(PlaceholderType.INSERT)
            .value(clientName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("clientAddress")
            .type(PlaceholderType.INSERT)
            .value(clientAddress)
            .build());

        placeholders.add(Placeholder.builder()
            .key("дата")
            .type(PlaceholderType.INSERT)
            .value(date)
            .build());

        // Данные о товаре/услуге
        placeholders.add(Placeholder.builder()
            .key("productName")
            .type(PlaceholderType.INSERT)
            .value(productName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("количество")
            .type(PlaceholderType.INSERT)
            .value(String.valueOf(quantity))
            .build());

        placeholders.add(Placeholder.builder()
            .key("цена")
            .type(PlaceholderType.INSERT)
            .value(String.format("%.2f", price))
            .build());

        placeholders.add(Placeholder.builder()
            .key("итого")
            .type(PlaceholderType.INSERT)
            .value(String.format("%.2f руб.", total))
            .build());

        TemplateData templateData = TemplateData.builder()
            .templateName(TEMPLATE_PATH)
            .placeholders(placeholders)
            .build();

        XMLSlideShow presentation = modificationService.modifyPresentation(templateData);
        String filename = String.format("invoice-%s.pptx", invoiceNumber);

        ResponseEntity<Resource> response = downloadService.prepareDownload(presentation, filename);
        presentation.close();

        log.info("Successfully generated invoice {}", invoiceNumber);
        return response;
    }

    /**
     * Генерирует счет с несколькими позициями товаров
     */
    public ResponseEntity<Resource> generateInvoiceMultipleItems(
        String invoiceNumber,
        String clientName,
        String clientAddress,
        String date,
        List<InvoiceItem> items
    ) throws Exception {

        log.info("Generating invoice {} with {} items", invoiceNumber, items.size());

        List<Placeholder> placeholders = new ArrayList<>();

        placeholders.add(Placeholder.builder()
            .key("invoiceNumber")
            .type(PlaceholderType.TEXT)
            .value(invoiceNumber)
            .build());

        placeholders.add(Placeholder.builder()
            .key("clientName")
            .type(PlaceholderType.INSERT)
            .value(clientName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("clientAddress")
            .type(PlaceholderType.INSERT)
            .value(clientAddress)
            .build());

        placeholders.add(Placeholder.builder()
            .key("дата")
            .type(PlaceholderType.INSERT)
            .value(date)
            .build());

        // Добавляем позиции товаров (предполагается, что в шаблоне есть $item1, $item2 и т.д.)
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            InvoiceItem item = items.get(i);
            double itemTotal = item.getQuantity() * item.getPrice();
            total += itemTotal;

            placeholders.add(Placeholder.builder()
                .key("item" + (i + 1) + "Name")
                .type(PlaceholderType.INSERT)
                .value(item.getName())
                .build());

            placeholders.add(Placeholder.builder()
                .key("item" + (i + 1) + "Quantity")
                .type(PlaceholderType.INSERT)
                .value(String.valueOf(item.getQuantity()))
                .build());

            placeholders.add(Placeholder.builder()
                .key("item" + (i + 1) + "Price")
                .type(PlaceholderType.INSERT)
                .value(String.format("%.2f", item.getPrice()))
                .build());

            placeholders.add(Placeholder.builder()
                .key("item" + (i + 1) + "Total")
                .type(PlaceholderType.INSERT)
                .value(String.format("%.2f", itemTotal))
                .build());
        }

        placeholders.add(Placeholder.builder()
            .key("grandTotal")
            .type(PlaceholderType.INSERT)
            .value(String.format("%.2f руб.", total))
            .build());

        TemplateData templateData = TemplateData.builder()
            .templateName(TEMPLATE_PATH)
            .placeholders(placeholders)
            .build();

        XMLSlideShow presentation = modificationService.modifyPresentation(templateData);
        String filename = String.format("invoice-%s.pptx", invoiceNumber);

        ResponseEntity<Resource> response = downloadService.prepareDownload(presentation, filename);
        presentation.close();

        return response;
    }

    // Вложенный класс для позиций счета
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class InvoiceItem {
        private String name;
        private int quantity;
        private double price;
    }
}