# Специализированные сервисы для отчетов

## Концепция

Вместо работы с generic API, вы можете создавать **специализированные сервисы** для каждого типа отчета. Каждый сервис:
- Работает со своим PPTX шаблоном
- Имеет конкретные параметры для своего типа отчета
- Автоматически заполняет нужные плейсхолдеры
- Предоставляет type-safe API

## Созданные сервисы

### 1. WeeklyReportService
**Шаблон:** `templates/weekly-report.pptx`

**Назначение:** Генерация еженедельных отчетов по проектам

**Пример шаблона:**
```
Slide 1:
  {{reportTitle}}  ← TEXT: полная замена заголовка

Slide 2:
  Проект: $projectName                    ← INSERT: вставка
  Неделя: $weekNumber                     ← INSERT: вставка
  Выполнено задач: $tasksCompleted        ← INSERT: вставка
  В работе: $tasksInProgress              ← INSERT: вставка
  {{chart}}                               ← IMAGE: график
```

**Использование в Java:**
```java
@Autowired
private WeeklyReportService weeklyReportService;

public void generateReport() throws Exception {
    ResponseEntity<Resource> report = weeklyReportService.generateWeeklyReport(
        42,                           // номер недели
        "Project Phoenix",            // название проекта
        25,                           // выполнено задач
        8,                            // задач в работе
        Path.of("chart.png")          // путь к графику
    );

    // Скачать или сохранить report
}
```

**API endpoint:**
```http
POST /api/reports/weekly/generate
Content-Type: application/json

{
  "weekNumber": 42,
  "projectName": "Project Phoenix",
  "tasksCompleted": 25,
  "tasksInProgress": 8,
  "chartImagePath": "/path/to/chart.png"
}
```

---

### 2. MonthlyReportService
**Шаблон:** `templates/monthly-report.pptx`

**Назначение:** Генерация месячных финансовых отчетов

**Пример шаблона:**
```
Slide 1:
  {{reportTitle}}  ← "Финансовый отчет за Декабрь 2024"
  {{logo}}         ← Логотип компании

Slide 2:
  Компания: $companyName       ← INSERT
  Месяц: $месяц                ← INSERT
  Год: $год                    ← INSERT

Slide 3:
  Выручка: $выручка            ← INSERT: "Выручка: 1,250,000 руб."
  Расходы: $расходы            ← INSERT: "Расходы: 800,000 руб."
  Прибыль: $прибыль            ← INSERT: "Прибыль: 450,000 руб."

  {{revenueChart}}             ← IMAGE: график выручки
  {{expensesChart}}            ← IMAGE: график расходов
```

**Использование в Java:**
```java
@Autowired
private MonthlyReportService monthlyReportService;

public void generateReport() throws Exception {
    ResponseEntity<Resource> report = monthlyReportService.generateMonthlyReport(
        "Декабрь",                      // месяц
        "2024",                         // год
        "ООО Рога и Копыта",            // компания
        "1,250,000 руб.",               // выручка
        "800,000 руб.",                 // расходы
        "450,000 руб.",                 // прибыль
        Path.of("logo.png"),            // логотип
        Path.of("revenue-chart.png"),   // график выручки
        Path.of("expenses-chart.png")   // график расходов
    );
}
```

**API endpoint:**
```http
POST /api/reports/monthly/generate
Content-Type: application/json

{
  "month": "Декабрь",
  "year": "2024",
  "companyName": "ООО Рога и Копыта",
  "revenue": "1,250,000 руб.",
  "expenses": "800,000 руб.",
  "profit": "450,000 руб.",
  "logoPath": "/path/to/logo.png",
  "revenueChartPath": "/path/to/revenue-chart.png",
  "expensesChartPath": "/path/to/expenses-chart.png"
}
```

---

### 3. InvoiceReportService
**Шаблон:** `templates/invoice-template.pptx`

**Назначение:** Генерация счетов (invoices)

**Пример шаблона:**
```
Slide 1:
  СЧЕТ НА ОПЛАТУ
  Номер: {{invoiceNumber}}     ← TEXT: полная замена

Slide 2:
  Клиент: $clientName          ← INSERT
  Адрес: $clientAddress        ← INSERT
  Дата: $дата                  ← INSERT

Slide 3:
  Товар: $productName          ← INSERT
  Количество: $количество шт.  ← INSERT
  Цена: $цена руб.             ← INSERT
  ИТОГО: $итого                ← INSERT
```

**Использование в Java:**
```java
@Autowired
private InvoiceReportService invoiceReportService;

public void generateInvoice() throws Exception {
    ResponseEntity<Resource> invoice = invoiceReportService.generateInvoice(
        "INV-2024-001",                 // номер счета
        "ООО Клиент",                   // имя клиента
        "г. Москва, ул. Ленина, 1",     // адрес
        "03.12.2024",                   // дата
        "Ноутбук Dell XPS 15",          // товар
        2,                              // количество
        85000.00,                       // цена за единицу
        170000.00                       // итого
    );
}

// Или с несколькими позициями:
public void generateInvoiceMulti() throws Exception {
    List<InvoiceReportService.InvoiceItem> items = List.of(
        InvoiceReportService.InvoiceItem.builder()
            .name("Ноутбук Dell XPS 15")
            .quantity(2)
            .price(85000.00)
            .build(),
        InvoiceReportService.InvoiceItem.builder()
            .name("Мышь Logitech MX Master")
            .quantity(2)
            .price(7000.00)
            .build()
    );

    ResponseEntity<Resource> invoice = invoiceReportService.generateInvoiceMultipleItems(
        "INV-2024-001",
        "ООО Клиент",
        "г. Москва, ул. Ленина, 1",
        "03.12.2024",
        items
    );
}
```

---

## Создание собственного специализированного сервиса

### Шаг 1: Создайте PPTX шаблон

**Пример:** `templates/quarterly-report.pptx`

```
Slide 1:
  {{title}}         ← Заголовок (полная замена)

Slide 2:
  Квартал: $quarter                 ← Вставка
  Год: $year                        ← Вставка
  Продажи: $sales руб.              ← Вставка
  Рост по сравнению с прошлым кварталом: $growth% ← Вставка
  {{salesChart}}                    ← График
```

### Шаг 2: Создайте сервис

```java
package com.example.reporting.service.report;

import com.example.reporting.model.*;
import com.example.reporting.service.*;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class QuarterlyReportService {

    private static final String TEMPLATE_PATH = "templates/quarterly-report.pptx";

    private final PptxModificationService modificationService;
    private final PptxDownloadService downloadService;

    public ResponseEntity<Resource> generateQuarterlyReport(
        String quarter,      // "Q1", "Q2", "Q3", "Q4"
        String year,
        String sales,
        String growth,
        Path salesChartPath
    ) throws Exception {

        log.info("Generating quarterly report for {} {}", quarter, year);

        List<Placeholder> placeholders = new ArrayList<>();

        // Заголовок
        placeholders.add(Placeholder.builder()
            .key("title")
            .type(PlaceholderType.TEXT)
            .value(String.format("Квартальный отчет %s %s", quarter, year))
            .build());

        // Данные (вставка в существующий текст)
        placeholders.add(Placeholder.builder()
            .key("quarter")
            .type(PlaceholderType.INSERT)
            .value(quarter)
            .build());

        placeholders.add(Placeholder.builder()
            .key("year")
            .type(PlaceholderType.INSERT)
            .value(year)
            .build());

        placeholders.add(Placeholder.builder()
            .key("sales")
            .type(PlaceholderType.INSERT)
            .value(sales)
            .build());

        placeholders.add(Placeholder.builder()
            .key("growth")
            .type(PlaceholderType.INSERT)
            .value(growth)
            .build());

        // График
        if (salesChartPath != null) {
            ImageData chart = ImageUtils.loadImageFromFile(salesChartPath);
            placeholders.add(Placeholder.builder()
                .key("salesChart")
                .type(PlaceholderType.IMAGE)
                .value(chart)
                .build());
        }

        TemplateData templateData = TemplateData.builder()
            .templateName(TEMPLATE_PATH)
            .placeholders(placeholders)
            .build();

        XMLSlideShow presentation = modificationService.modifyPresentation(templateData);
        String filename = String.format("quarterly-report-%s-%s.pptx", quarter, year);

        ResponseEntity<Resource> response = downloadService.prepareDownload(presentation, filename);
        presentation.close();

        log.info("Successfully generated quarterly report");
        return response;
    }
}
```

### Шаг 3: Создайте контроллер (опционально)

```java
package com.example.reporting.controller;

import com.example.reporting.service.report.QuarterlyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/reports/quarterly")
@RequiredArgsConstructor
public class QuarterlyReportController {

    private final QuarterlyReportService quarterlyReportService;

    @PostMapping("/generate")
    public ResponseEntity<Resource> generate(@RequestBody QuarterlyRequest request) {
        try {
            return quarterlyReportService.generateQuarterlyReport(
                request.getQuarter(),
                request.getYear(),
                request.getSales(),
                request.getGrowth(),
                request.getChartPath() != null ? Path.of(request.getChartPath()) : null
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @lombok.Data
    public static class QuarterlyRequest {
        private String quarter;
        private String year;
        private String sales;
        private String growth;
        private String chartPath;
    }
}
```

---

## Преимущества специализированных сервисов

✅ **Type Safety** - параметры типизированы, меньше ошибок
✅ **Читаемость** - понятно, какие данные нужны для каждого отчета
✅ **Переиспользование** - можно вызывать из разных мест
✅ **Тестируемость** - легко писать unit-тесты
✅ **Документация** - параметры самодокументируются
✅ **Изоляция** - изменения в одном отчете не влияют на другие

## Рекомендации

1. **Один сервис = один тип отчета**
2. **Явные параметры вместо Map** (для type safety)
3. **Логирование** для отладки
4. **Валидация данных** перед генерацией
5. **Закрывайте XMLSlideShow** после использования
6. **Используйте Builder pattern** для сложных объектов

## Структура проекта

```
src/main/java/com/example/reporting/
├── service/
│   └── report/
│       ├── WeeklyReportService.java       ← Еженедельные отчеты
│       ├── MonthlyReportService.java      ← Месячные отчеты
│       ├── InvoiceReportService.java      ← Счета
│       ├── QuarterlyReportService.java    ← Ваш новый сервис
│       └── AnnualReportService.java       ← Годовые отчеты
│
└── controller/
    ├── WeeklyReportController.java        ← REST API для еженедельных
    ├── MonthlyReportController.java       ← REST API для месячных
    └── ...

src/main/resources/templates/
├── weekly-report.pptx
├── monthly-report.pptx
├── invoice-template.pptx
├── quarterly-report.pptx
└── annual-report.pptx
```

## Пример комплексного использования

```java
@Service
@RequiredArgsConstructor
public class ReportingFacade {

    private final WeeklyReportService weeklyReportService;
    private final MonthlyReportService monthlyReportService;
    private final InvoiceReportService invoiceReportService;

    public void generateAllReportsForMonth(int month, int year) throws Exception {
        // 1. Генерируем все еженедельные отчеты за месяц
        for (int week = 1; week <= 4; week++) {
            weeklyReportService.generateWeeklyReport(/* ... */);
        }

        // 2. Генерируем месячный отчет
        monthlyReportService.generateMonthlyReport(/* ... */);

        // 3. Генерируем все инвойсы за месяц
        List<InvoiceData> invoices = loadInvoicesForMonth(month, year);
        for (InvoiceData invoice : invoices) {
            invoiceReportService.generateInvoice(/* ... */);
        }
    }
}
```

---

## Итого

Вместо универсального API с PlaceholderType, вы создаете **отдельный сервис для каждого типа отчета**. Каждый сервис:
- Знает свой шаблон
- Имеет специфические параметры
- Автоматически собирает плейсхолдеры
- Возвращает готовый файл

Это более безопасный и удобный подход для реальных приложений!