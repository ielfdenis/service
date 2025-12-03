# Архитектура системы обработки PPTX

## Обзор

Система построена на основе паттерна **Strategy** для обеспечения расширяемости и гибкости при работе с различными типами данных в PPTX шаблонах.

## Диаграмма компонентов

```
┌─────────────────────┐
│  PptxController     │ REST API endpoints
└──────────┬──────────┘
           │
           ├─────────────┐
           │             │
           ▼             ▼
┌──────────────┐  ┌──────────────────┐
│ PptxReader   │  │ PptxModification │
│ Service      │  │ Service          │
└──────────────┘  └────────┬─────────┘
                           │
                           │ uses
                           ▼
                  ┌────────────────────┐
                  │ PlaceholderReplacer│ <<interface>>
                  └────────┬───────────┘
                           │
                ┌──────────┴──────────┐
                │                     │
                ▼                     ▼
    ┌───────────────────┐  ┌────────────────────┐
    │ TextPlaceholder   │  │ ImagePlaceholder   │
    │ Replacer          │  │ Replacer           │
    └───────────────────┘  └────────────────────┘
```

## Ключевые компоненты

### 1. Controller Layer

**`PptxController`**
- Предоставляет REST API для работы с шаблонами
- Endpoints:
  - `GET /api/pptx/placeholders` - извлечение плейсхолдеров
  - `POST /api/pptx/generate` - генерация с скачиванием
  - `POST /api/pptx/modify` - модификация (возврат байтов)

### 2. Service Layer

**`PptxReaderService`**
- Загрузка PPTX шаблонов из resources
- Парсинг плейсхолдеров формата `{{key}}`
- Использует `ResourceLoader` для доступа к classpath

**`PptxModificationService`**
- Координирует процесс замены плейсхолдеров
- Делегирует работу соответствующим replacers
- Использует Strategy pattern для выбора обработчика

**`PptxDownloadService`**
- Конвертация XMLSlideShow в байты
- Подготовка HTTP response для скачивания
- Установка правильных headers

### 3. Replacer Layer (Strategy Pattern)

**`PlaceholderReplacer` (interface)**
```java
public interface PlaceholderReplacer {
    boolean canHandle(Placeholder placeholder);
    void replace(XMLSlideShow presentation, Placeholder placeholder);
}
```

**`TextPlaceholderReplacer`**
- Обрабатывает текстовые плейсхолдеры
- Проходит по всем слайдам и текстовым элементам
- Выполняет замену через `XSLFTextRun.setText()`

**`ImagePlaceholderReplacer`**
- Обрабатывает плейсхолдеры изображений
- Находит текстовый элемент с плейсхолдером
- Заменяет текстовый элемент на изображение
- Сохраняет позицию и размер оригинального элемента

### 4. Model Layer

**`Placeholder`**
- Универсальная модель для любого типа данных
- Поля: key, type, value (Object)

**`PlaceholderType` (enum)**
- TEXT - текстовые данные
- IMAGE - изображения
- Расширяемо для новых типов

**`TemplateData`**
- DTO для API запросов
- Содержит имя шаблона и список плейсхолдеров

**`ImageData`**
- Инкапсулирует данные изображения
- Поля: imageBytes, contentType, pictureType

### 5. Utility Layer

**`ImageUtils`**
- Определение типа изображения по MIME
- Загрузка изображений из файлов
- Конвертация в формат Apache POI

## Паттерны проектирования

### Strategy Pattern

Используется для замены плейсхолдеров:

```java
// Интерфейс стратегии
PlaceholderReplacer

// Конкретные стратегии
TextPlaceholderReplacer
ImagePlaceholderReplacer

// Контекст
PptxModificationService
```

**Преимущества:**
- Легко добавлять новые типы плейсхолдеров
- Каждый replacer независим и тестируем
- Автоматическая регистрация через Spring DI

### Dependency Injection

Spring автоматически:
- Находит все `@Component` реализующие `PlaceholderReplacer`
- Инжектирует `List<PlaceholderReplacer>` в `PptxModificationService`
- Новые replacers добавляются без изменения существующего кода

## Поток данных

### 1. Извлечение плейсхолдеров

```
User Request → Controller → PptxReaderService
    ↓
Load Template from classpath
    ↓
Parse slides & shapes
    ↓
Extract {{placeholders}}
    ↓
Return List<String>
```

### 2. Генерация презентации

```
User Request (TemplateData)
    ↓
Controller → PptxModificationService
    ↓
Load Template
    ↓
For each Placeholder:
    ├─ Find matching Replacer (canHandle)
    └─ Execute replace()
    ↓
PptxDownloadService
    ↓
Convert to bytes + HTTP headers
    ↓
Download to User
```

## Расширяемость

### Добавление нового типа плейсхолдера

1. **Добавить тип в enum:**
```java
public enum PlaceholderType {
    TEXT,
    IMAGE,
    TABLE  // новый тип
}
```

2. **Создать replacer:**
```java
@Component
public class TablePlaceholderReplacer implements PlaceholderReplacer {
    @Override
    public boolean canHandle(Placeholder p) {
        return p.getType() == PlaceholderType.TABLE;
    }

    @Override
    public void replace(XMLSlideShow pres, Placeholder p) {
        // логика замены таблицы
    }
}
```

3. **Готово!** Spring автоматически зарегистрирует новый replacer.

### Добавление новых endpoints

Расширяйте `PptxController` новыми методами:
- Batch processing
- Async generation
- Template validation
- Preview generation

## Безопасность и производительность

### Текущая реализация

- Шаблоны загружаются из classpath (защита от path traversal)
- Обработка в памяти (быстро, но ограничено RAM)
- Синхронная обработка

### Рекомендации для production

1. **Асинхронная обработка:**
```java
@Async
public CompletableFuture<byte[]> generateAsync(TemplateData data)
```

2. **Кэширование шаблонов:**
```java
@Cacheable("templates")
public XMLSlideShow loadTemplate(String path)
```

3. **Ограничение размера файлов:**
```java
@RequestBody(maxSize = 10MB) TemplateData data
```

4. **Валидация входных данных:**
```java
@Valid @RequestBody TemplateData data
```

## Тестирование

### Unit Tests

- `TextPlaceholderReplacerTest` - тестирование замены текста
- Создание in-memory презентаций для тестов
- Изоляция каждого replacer

### Integration Tests

Рекомендуется добавить:
- Тесты REST endpoints с `@SpringBootTest`
- Тесты с реальными PPTX файлами
- Тесты производительности

## Зависимости

- **Apache POI 5.2.5** - работа с PPTX
- **Spring Boot 4.0.0** - фреймворк
- **Lombok** - уменьшение boilerplate кода

## Логирование

Все сервисы используют SLF4J через `@Slf4j`:
- INFO - успешные операции
- WARN - отсутствующие replacers
- ERROR - исключения при обработке

## Дальнейшее развитие

### Возможные улучшения

1. **Chart Support** - замена диаграмм
2. **Table Support** - работа с таблицами
3. **Batch Processing** - обработка нескольких шаблонов
4. **Template Validation** - проверка корректности шаблонов
5. **Async Processing** - асинхронная генерация
6. **Caching** - кэширование шаблонов и результатов
7. **Metrics** - мониторинг производительности
8. **Versioning** - версионирование шаблонов