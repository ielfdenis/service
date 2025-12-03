# Финальное резюме проекта

## Что было создано

Полнофункциональная система для работы с PPTX шаблонами на Spring Boot с Apache POI.

## ✅ Основные возможности

### 1. Три типа плейсхолдеров

#### TEXT - Полная замена
```
Шаблон: {{заголовок}}
Результат: текст полностью заменяется
```

#### INSERT - Вставка значения
```
Шаблон: Цена: $цена руб.
Результат: Цена: 1000 руб.
```

#### IMAGE - Замена на изображение
```
Шаблон: {{logo}} (в текстовом поле)
Результат: изображение
```

### 2. Базовый универсальный сервис

`BaseReportService` - общий метод для всех типов отчетов:

```java
public ResponseEntity<Resource> generateReport(
    String templatePath,
    List<Placeholder> placeholders,
    String outputFilename
) throws Exception
```

### 3. Специализированные сервисы

Каждый тип отчета имеет свой сервис:
- `WeeklyReportService` - еженедельные отчеты
- `MonthlyReportService` - месячные отчеты
- `InvoiceReportService` - счета/инвойсы

Каждый сервис:
- Работает со своим шаблоном
- Имеет специфические параметры
- Использует `BaseReportService.generateReport()`

## 📁 Структура проекта

```
src/main/java/com/example/reporting/
├── controller/
│   ├── PptxController.java              # Generic API
│   ├── WeeklyReportController.java      # Еженедельные отчеты
│   └── MonthlyReportController.java     # Месячные отчеты
│
├── service/
│   ├── PptxReaderService.java           # Чтение PPTX
│   ├── PptxModificationService.java     # Модификация
│   ├── PptxDownloadService.java         # Скачивание
│   │
│   └── report/
│       ├── BaseReportService.java       # 🔥 БАЗОВЫЙ СЕРВИС
│       ├── WeeklyReportService.java     # Пример 1
│       ├── MonthlyReportService.java    # Пример 2
│       └── InvoiceReportService.java    # Пример 3
│
├── service/replacer/
│   ├── PlaceholderReplacer.java         # Интерфейс
│   ├── TextPlaceholderReplacer.java     # TEXT тип
│   ├── InsertPlaceholderReplacer.java   # INSERT тип  ← НОВОЕ!
│   └── ImagePlaceholderReplacer.java    # IMAGE тип
│
├── model/
│   ├── Placeholder.java
│   ├── PlaceholderType.java             # TEXT, INSERT, IMAGE
│   ├── TemplateData.java
│   └── ImageData.java
│
└── util/
    └── ImageUtils.java

src/main/resources/templates/
└── (ваши PPTX шаблоны)
```

## 🚀 Как создать новый тип отчета

### Шаг 1: Создайте PPTX шаблон

`src/main/resources/templates/my-report.pptx`:

```
Slide 1:
  {{title}}           ← TEXT: полная замена

Slide 2:
  Дата: $date         ← INSERT: вставка
  Цена: $price руб.   ← INSERT: вставка
  {{chart}}           ← IMAGE: изображение
```

### Шаг 2: Создайте сервис

```java
package com.example.reporting.service.report;

import com.example.reporting.model.*;
import com.example.reporting.util.ImageUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class MyReportService {

    private static final String TEMPLATE_PATH = "templates/my-report.pptx";
    private final BaseReportService baseReportService;

    public MyReportService(BaseReportService baseReportService) {
        this.baseReportService = baseReportService;
    }

    public ResponseEntity<Resource> generateMyReport(
        String title,
        String date,
        String price,
        Path chartPath
    ) throws Exception {

        List<Placeholder> placeholders = new ArrayList<>();

        // TEXT - полная замена
        Placeholder titlePlaceholder = new Placeholder();
        titlePlaceholder.setKey("title");
        titlePlaceholder.setType(PlaceholderType.TEXT);
        titlePlaceholder.setValue(title);
        placeholders.add(titlePlaceholder);

        // INSERT - вставка значения
        Placeholder datePlaceholder = new Placeholder();
        datePlaceholder.setKey("date");
        datePlaceholder.setType(PlaceholderType.INSERT);
        datePlaceholder.setValue(date);
        placeholders.add(datePlaceholder);

        Placeholder pricePlaceholder = new Placeholder();
        pricePlaceholder.setKey("price");
        pricePlaceholder.setType(PlaceholderType.INSERT);
        pricePlaceholder.setValue(price);
        placeholders.add(pricePlaceholder);

        // IMAGE - изображение
        if (chartPath != null) {
            ImageData chart = ImageUtils.loadImageFromFile(chartPath);
            Placeholder chartPlaceholder = new Placeholder();
            chartPlaceholder.setKey("chart");
            chartPlaceholder.setType(PlaceholderType.IMAGE);
            chartPlaceholder.setValue(chart);
            placeholders.add(chartPlaceholder);
        }

        // Вызываем БАЗОВЫЙ МЕТОД
        return baseReportService.generateReport(
            TEMPLATE_PATH,
            placeholders,
            "my-report.pptx"
        );
    }
}
```

### Шаг 3: Готово!

Теперь можете использовать:

```java
@Autowired
private MyReportService myReportService;

public void test() throws Exception {
    ResponseEntity<Resource> report = myReportService.generateMyReport(
        "Мой отчет",
        "03.12.2024",
        "1000",
        Path.of("chart.png")
    );
}
```

## 📚 Документация

| Файл | Описание |
|------|----------|
| `README_RU.md` | Быстрый старт |
| `PPTX_USAGE.md` | Детальная документация по API |
| `INSERT_USAGE.md` | Подробности про INSERT тип |
| `BASE_SERVICE_USAGE.md` | Как использовать базовый сервис |
| `SPECIALIZED_SERVICES.md` | Примеры специализированных сервисов |
| `API_EXAMPLES.http` | Готовые HTTP запросы |
| `ARCHITECTURE.md` | Техническая архитектура |

## 🎯 Ключевые принципы

### 1. Один шаблон = один сервис

Для каждого типа отчета создается отдельный сервис со своими параметрами.

### 2. Базовый метод для всех

Все сервисы используют `BaseReportService.generateReport()`:

```java
baseReportService.generateReport(templatePath, placeholders, filename)
```

### 3. Типизированные параметры

Вместо Map используются явные параметры:

```java
// ✅ Хорошо
public ResponseEntity<Resource> generate(String name, int age, Path photo)

// ❌ Плохо
public ResponseEntity<Resource> generate(Map<String, Object> data)
```

### 4. Расширяемость

Легко добавить новый тип плейсхолдера - создайте класс `@Component` с `PlaceholderReplacer`:

```java
@Component
public class TablePlaceholderReplacer implements PlaceholderReplacer {
    @Override
    public boolean canHandle(Placeholder placeholder) {
        return placeholder.getType() == PlaceholderType.TABLE;
    }

    @Override
    public void replace(XMLSlideShow presentation, Placeholder placeholder) {
        // Ваша логика
    }
}
```

Spring автоматически зарегистрирует новый replacer.

## 🔧 Команды Maven

```bash
# Установка зависимостей
./mvnw clean install

# Запуск приложения
./mvnw spring-boot:run

# Запуск тестов
./mvnw test

# Сборка JAR
./mvnw package
```

## 📝 Примеры шаблонов

### Еженедельный отчет

```
Slide 1:
  {{reportTitle}}  ← заголовок

Slide 2:
  Проект: $projectName
  Неделя: $weekNumber
  Выполнено: $tasksCompleted
  В работе: $tasksInProgress
  {{chart}}  ← график
```

### Месячный отчет

```
Slide 1:
  {{reportTitle}}  ← заголовок
  {{logo}}  ← логотип

Slide 2:
  Компания: $companyName
  Месяц: $месяц
  Год: $год

Slide 3:
  Выручка: $выручка
  Расходы: $расходы
  Прибыль: $прибыль
  {{revenueChart}}  ← график
```

### Счет/Invoice

```
Slide 1:
  СЧЕТ НА ОПЛАТУ
  Номер: {{invoiceNumber}}

Slide 2:
  Клиент: $clientName
  Адрес: $clientAddress
  Дата: $дата

Slide 3:
  Товар: $productName
  Количество: $количество шт.
  Цена: $цена
  ИТОГО: $итого
```

## 🎨 Преимущества архитектуры

✅ **DRY** - логика генерации в одном месте (BaseReportService)
✅ **Type Safety** - параметры типизированы
✅ **Расширяемость** - легко добавлять новые типы
✅ **Читаемость** - понятно, что делает каждый сервис
✅ **Тестируемость** - легко писать unit-тесты
✅ **Переиспользование** - базовый метод используют все

## 🏁 Итого

У вас есть:

1. ✅ **Универсальный движок** - `BaseReportService`
2. ✅ **Три типа плейсхолдеров** - TEXT, INSERT, IMAGE
3. ✅ **Примеры сервисов** - Weekly, Monthly, Invoice
4. ✅ **REST API** - generic и specialized endpoints
5. ✅ **Расширяемая архитектура** - Strategy pattern
6. ✅ **Полная документация** - 7 документов

**Чтобы создать новый отчет:**
1. Создайте PPTX шаблон
2. Создайте сервис, используя `BaseReportService`
3. Соберите `List<Placeholder>` с вашими данными
4. Вызовите `baseReportService.generateReport()`
5. Готово!

**Система готова к работе!** 🚀