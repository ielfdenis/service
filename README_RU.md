# Система обработки PPTX шаблонов

## Описание

Приложение для работы с PowerPoint шаблонами: чтение, изменение и генерация PPTX файлов с автоматической заменой текста и изображений.

## Быстрый старт

### 1. Установка зависимостей

```bash
./mvnw clean install
```

### 2. Запуск приложения

```bash
./mvnw spring-boot:run
```

Приложение запустится на http://localhost:8080

### 3. Подготовка шаблона

1. Создайте PPTX файл в PowerPoint
2. Вставьте плейсхолдеры в формате `{{имя_переменной}}`
3. Сохраните файл в `src/main/resources/templates/`

Пример:
- Текст: `Компания: {{companyName}}`
- Изображение: Создайте текстовое поле с `{{logo}}`

## API Endpoints

### Получить список плейсхолдеров

```bash
curl "http://localhost:8080/api/pptx/placeholders?templatePath=templates/report.pptx"
```

### Сгенерировать презентацию

```bash
curl -X POST http://localhost:8080/api/pptx/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "templates/report.pptx",
    "placeholders": [
      {
        "key": "companyName",
        "type": "TEXT",
        "value": "ООО Рога и Копыта"
      },
      {
        "key": "reportTitle",
        "type": "TEXT",
        "value": "Отчет за 4 квартал 2024"
      }
    ]
  }' \
  --output result.pptx
```

## Архитектура

### Основные компоненты

1. **Модели** (`model/`)
   - `Placeholder` - плейсхолдер с ключом, типом и значением
   - `PlaceholderType` - тип: TEXT или IMAGE
   - `TemplateData` - данные шаблона
   - `ImageData` - данные изображения

2. **Сервисы** (`service/`)
   - `PptxReaderService` - чтение шаблонов
   - `PptxModificationService` - модификация презентаций
   - `PptxDownloadService` - подготовка к скачиванию

3. **Replacers** (`service/replacer/`)
   - `TextPlaceholderReplacer` - замена текста
   - `ImagePlaceholderReplacer` - замена изображений

### Расширение функциональности

Чтобы добавить новый тип плейсхолдера:

```java
@Component
public class TablePlaceholderReplacer implements PlaceholderReplacer {

    @Override
    public boolean canHandle(Placeholder placeholder) {
        return placeholder.getType() == PlaceholderType.TABLE;
    }

    @Override
    public void replace(XMLSlideShow presentation, Placeholder placeholder) {
        // Ваша реализация
    }
}
```

Spring автоматически зарегистрирует новый replacer.

## Примеры использования

### Java код

```java
@Autowired
private PptxModificationService modificationService;

public void createReport() throws Exception {
    List<Placeholder> placeholders = List.of(
        Placeholder.builder()
            .key("title")
            .type(PlaceholderType.TEXT)
            .value("Заголовок отчета")
            .build(),
        Placeholder.builder()
            .key("logo")
            .type(PlaceholderType.IMAGE)
            .value(ImageUtils.loadImageFromFile(Path.of("logo.png")))
            .build()
    );

    TemplateData data = TemplateData.builder()
        .templateName("templates/report.pptx")
        .placeholders(placeholders)
        .build();

    XMLSlideShow presentation = modificationService.modifyPresentation(data);
    // Работа с презентацией
}
```

## Поддерживаемые форматы изображений

- PNG
- JPEG/JPG
- GIF
- BMP
- TIFF

## Структура проекта

```
src/main/java/com/example/reporting/
├── controller/          # REST контроллеры
├── service/            # Бизнес-логика
│   └── replacer/       # Стратегии замены
├── model/              # Модели данных
├── util/               # Утилиты
└── exception/          # Исключения

src/main/resources/
├── templates/          # PPTX шаблоны
└── application.properties
```

## Полезные ссылки

- Детальная документация: [PPTX_USAGE.md](PPTX_USAGE.md)
- Примеры API запросов: [API_EXAMPLES.http](API_EXAMPLES.http)
- Архитектура проекта: [CLAUDE.md](CLAUDE.md)