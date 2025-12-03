# Использование базового сервиса (BaseReportService)

## Концепция

`BaseReportService` - это **универсальный базовый сервис**, который содержит общую логику генерации PPTX отчетов. Все специализированные сервисы используют его для финальной генерации.

## Преимущества

✅ **DRY (Don't Repeat Yourself)** - логика генерации в одном месте
✅ **Легкость создания новых отчетов** - просто соберите плейсхолдеры и вызовите базовый метод
✅ **Единообразие** - все отчеты генерируются одинаково
✅ **Простота поддержки** - изменения в одном месте

## Методы BaseReportService

### 1. `generateReport()` - Основной метод

Генерирует отчет и возвращает `ResponseEntity<Resource>` для скачивания.

```java
public ResponseEntity<Resource> generateReport(
    String templatePath,      // Путь к шаблону
    List<Placeholder> placeholders,  // Данные для вставки
    String outputFilename     // Имя файла результата
) throws Exception
```

### 2. `generateReportBytes()` - Получение байтов

Возвращает `byte[]` вместо ResponseEntity. Полезно для сохранения в БД или файловую систему.

```java
public byte[] generateReportBytes(
    String templatePath,
    List<Placeholder> placeholders
) throws Exception
```

### 3. `generateReportPresentation()` - Получение XMLSlideShow

Возвращает `XMLSlideShow` для дополнительной обработки.

```java
public XMLSlideShow generateReportPresentation(
    String templatePath,
    List<Placeholder> placeholders
) throws Exception
```

---

## Примеры использования

### Пример 1: Простой отчет

```java
@Service
@RequiredArgsConstructor
public class SimpleReportService {

    private final BaseReportService baseReportService;

    public ResponseEntity<Resource> generateSimpleReport(String title, String date) throws Exception {
        List<Placeholder> placeholders = List.of(
            Placeholder.builder()
                .key("title")
                .type(PlaceholderType.TEXT)
                .value(title)
                .build(),
            Placeholder.builder()
                .key("date")
                .type(PlaceholderType.INSERT)
                .value(date)
                .build()
        );

        return baseReportService.generateReport(
            "templates/simple-report.pptx",
            placeholders,
            "simple-report.pptx"
        );
    }
}
```

### Пример 2: Отчет с изображениями

```java
@Service
@RequiredArgsConstructor
public class ProductReportService {

    private final BaseReportService baseReportService;

    public ResponseEntity<Resource> generateProductReport(
        String productName,
        String price,
        Path productImagePath
    ) throws Exception {

        List<Placeholder> placeholders = new ArrayList<>();

        placeholders.add(Placeholder.builder()
            .key("productName")
            .type(PlaceholderType.INSERT)
            .value(productName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("price")
            .type(PlaceholderType.INSERT)
            .value(price)
            .build());

        if (productImagePath != null) {
            ImageData image = ImageUtils.loadImageFromFile(productImagePath);
            placeholders.add(Placeholder.builder()
                .key("productImage")
                .type(PlaceholderType.IMAGE)
                .value(image)
                .build());
        }

        return baseReportService.generateReport(
            "templates/product-report.pptx",
            placeholders,
            "product-report-" + productName + ".pptx"
        );
    }
}
```

### Пример 3: Сохранение в файл вместо скачивания

```java
@Service
@RequiredArgsConstructor
public class ArchiveReportService {

    private final BaseReportService baseReportService;

    public void saveReportToFile(String title, Path outputPath) throws Exception {
        List<Placeholder> placeholders = List.of(
            Placeholder.builder()
                .key("title")
                .type(PlaceholderType.TEXT)
                .value(title)
                .build()
        );

        // Получаем байты
        byte[] reportBytes = baseReportService.generateReportBytes(
            "templates/archive-report.pptx",
            placeholders
        );

        // Сохраняем в файл
        Files.write(outputPath, reportBytes);
    }
}
```

### Пример 4: Дополнительная обработка

```java
@Service
@RequiredArgsConstructor
public class CustomReportService {

    private final BaseReportService baseReportService;
    private final PptxDownloadService downloadService;

    public ResponseEntity<Resource> generateWithCustomProcessing(String data) throws Exception {
        List<Placeholder> placeholders = List.of(
            Placeholder.builder()
                .key("data")
                .type(PlaceholderType.INSERT)
                .value(data)
                .build()
        );

        // Получаем презентацию
        XMLSlideShow presentation = baseReportService.generateReportPresentation(
            "templates/custom-report.pptx",
            placeholders
        );

        // Дополнительная обработка
        // Например, добавляем водяной знак, меняем тему и т.д.
        addWatermark(presentation);

        // Возвращаем для скачивания
        ResponseEntity<Resource> response = downloadService.prepareDownload(
            presentation,
            "custom-report.pptx"
        );

        presentation.close();
        return response;
    }

    private void addWatermark(XMLSlideShow presentation) {
        // Ваша логика добавления водяного знака
    }
}
```

---

## Паттерн создания специализированного сервиса

Вот универсальный шаблон для создания любого нового отчета:

```java
package com.example.reporting.service.report;

import com.example.reporting.model.*;
import com.example.reporting.util.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class YourCustomReportService {

    // 1. Указываем путь к шаблону
    private static final String TEMPLATE_PATH = "templates/your-template.pptx";

    // 2. Инжектим базовый сервис
    private final BaseReportService baseReportService;

    // 3. Создаем метод с нужными параметрами
    public ResponseEntity<Resource> generateYourReport(
        String param1,
        String param2,
        int param3,
        Path imagePath
    ) throws Exception {

        log.info("Generating your custom report with params: {}, {}, {}", param1, param2, param3);

        // 4. Собираем плейсхолдеры
        List<Placeholder> placeholders = new ArrayList<>();

        placeholders.add(Placeholder.builder()
            .key("param1")
            .type(PlaceholderType.TEXT)  // или INSERT
            .value(param1)
            .build());

        placeholders.add(Placeholder.builder()
            .key("param2")
            .type(PlaceholderType.INSERT)
            .value(param2)
            .build());

        placeholders.add(Placeholder.builder()
            .key("param3")
            .type(PlaceholderType.INSERT)
            .value(String.valueOf(param3))
            .build());

        // Добавляем изображение, если есть
        if (imagePath != null) {
            ImageData image = ImageUtils.loadImageFromFile(imagePath);
            placeholders.add(Placeholder.builder()
                .key("image")
                .type(PlaceholderType.IMAGE)
                .value(image)
                .build());
        }

        // 5. Вызываем базовый метод
        String filename = "your-report.pptx";
        return baseReportService.generateReport(TEMPLATE_PATH, placeholders, filename);
    }
}
```

---

## Реальный пример: Отчет о продажах

### Шаг 1: Создаем PPTX шаблон

`src/main/resources/templates/sales-report.pptx`:

```
Slide 1:
  {{title}}  ← Заголовок (TEXT)

Slide 2:
  Магазин: $storeName        ← INSERT
  Месяц: $month              ← INSERT
  Продажи: $sales руб.       ← INSERT
  Клиентов: $customers       ← INSERT

Slide 3:
  {{salesChart}}             ← График продаж (IMAGE)
```

### Шаг 2: Создаем сервис

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class SalesReportService {

    private static final String TEMPLATE_PATH = "templates/sales-report.pptx";
    private final BaseReportService baseReportService;

    public ResponseEntity<Resource> generateSalesReport(
        String storeName,
        String month,
        double sales,
        int customers,
        Path chartPath
    ) throws Exception {

        log.info("Generating sales report for store: {}, month: {}", storeName, month);

        List<Placeholder> placeholders = new ArrayList<>();

        // Заголовок
        placeholders.add(Placeholder.builder()
            .key("title")
            .type(PlaceholderType.TEXT)
            .value(String.format("Отчет о продажах - %s %s", storeName, month))
            .build());

        // Данные
        placeholders.add(Placeholder.builder()
            .key("storeName")
            .type(PlaceholderType.INSERT)
            .value(storeName)
            .build());

        placeholders.add(Placeholder.builder()
            .key("month")
            .type(PlaceholderType.INSERT)
            .value(month)
            .build());

        placeholders.add(Placeholder.builder()
            .key("sales")
            .type(PlaceholderType.INSERT)
            .value(String.format("%.2f", sales))
            .build());

        placeholders.add(Placeholder.builder()
            .key("customers")
            .type(PlaceholderType.INSERT)
            .value(String.valueOf(customers))
            .build());

        // График
        if (chartPath != null) {
            ImageData chart = ImageUtils.loadImageFromFile(chartPath);
            placeholders.add(Placeholder.builder()
                .key("salesChart")
                .type(PlaceholderType.IMAGE)
                .value(chart)
                .build());
        }

        // Генерируем через базовый сервис
        String filename = String.format("sales-report-%s-%s.pptx", storeName, month);
        return baseReportService.generateReport(TEMPLATE_PATH, placeholders, filename);
    }
}
```

### Шаг 3: Используем в контроллере

```java
@RestController
@RequestMapping("/api/reports/sales")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService salesReportService;

    @PostMapping("/generate")
    public ResponseEntity<Resource> generate(@RequestBody SalesReportRequest request) {
        try {
            return salesReportService.generateSalesReport(
                request.getStoreName(),
                request.getMonth(),
                request.getSales(),
                request.getCustomers(),
                request.getChartPath() != null ? Path.of(request.getChartPath()) : null
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @lombok.Data
    public static class SalesReportRequest {
        private String storeName;
        private String month;
        private double sales;
        private int customers;
        private String chartPath;
    }
}
```

---

## Итого

### Структура работы

1. **Создаете PPTX шаблон** с плейсхолдерами
2. **Создаете специализированный сервис**
   - Инжектите `BaseReportService`
   - Определяете параметры метода
   - Собираете `List<Placeholder>`
   - Вызываете `baseReportService.generateReport()`
3. **Создаете контроллер** (опционально)
4. **Готово!**

### Преимущества такого подхода

✅ Не нужно дублировать код генерации
✅ Все сервисы работают единообразно
✅ Легко создавать новые типы отчетов
✅ Type-safe параметры для каждого отчета
✅ Базовый сервис можно легко расширять

### Что делает BaseReportService

- Принимает шаблон, плейсхолдеры и имя файла
- Вызывает `PptxModificationService`
- Обрабатывает все плейсхолдеры
- Готовит файл для скачивания
- Закрывает ресурсы
- Логирует процесс

**Вы просто собираете данные → передаете в базовый сервис → получаете готовый отчет!**