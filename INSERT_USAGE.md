# Использование INSERT плейсхолдеров

## Отличие INSERT от TEXT

### TEXT (полная замена)
- Паттерн: `{{ключ}}`
- Действие: Весь текст с плейсхолдером **полностью заменяется** на значение
- Пример: `{{цена}}` → `1000`

### INSERT (вставка значения)
- Паттерн: `$ключ`
- Действие: Только плейсхолдер **заменяется**, остальной текст **сохраняется**
- Пример: `Цена: $цена руб.` → `Цена: 1000 руб.`

## Примеры использования

### Пример 1: Простая вставка

**В шаблоне PPTX:**
```
Цена товара: $цена
```

**API запрос:**
```json
{
  "templateName": "templates/price.pptx",
  "placeholders": [
    {
      "key": "цена",
      "type": "INSERT",
      "value": "1000 руб."
    }
  ]
}
```

**Результат:**
```
Цена товара: 1000 руб.
```

### Пример 2: Множественные вставки

**В шаблоне PPTX:**
```
Продукт: $продукт
Цена: $цена
Количество: $количество шт.
Итого: $итого
```

**API запрос:**
```json
{
  "templateName": "templates/invoice.pptx",
  "placeholders": [
    {
      "key": "продукт",
      "type": "INSERT",
      "value": "Ноутбук Dell XPS 15"
    },
    {
      "key": "цена",
      "type": "INSERT",
      "value": "85000"
    },
    {
      "key": "количество",
      "type": "INSERT",
      "value": "2"
    },
    {
      "key": "итого",
      "type": "INSERT",
      "value": "170000 руб."
    }
  ]
}
```

**Результат:**
```
Продукт: Ноутбук Dell XPS 15
Цена: 85000
Количество: 2 шт.
Итого: 170000 руб.
```

### Пример 3: Вставка внутри предложения

**В шаблоне PPTX:**
```
Компания $компания достигла выручки в размере $выручка за $период.
```

**API запрос:**
```json
{
  "placeholders": [
    {
      "key": "компания",
      "type": "INSERT",
      "value": "ООО \"Рога и Копыта\""
    },
    {
      "key": "выручка",
      "type": "INSERT",
      "value": "15 млн руб."
    },
    {
      "key": "период",
      "type": "INSERT",
      "value": "Q4 2024"
    }
  ]
}
```

**Результат:**
```
Компания ООО "Рога и Копыта" достигла выручки в размере 15 млн руб. за Q4 2024.
```

### Пример 4: Комбинация TEXT и INSERT

**В шаблоне PPTX:**
```
Slide 1:
  {{заголовок}}           ← TEXT: полная замена

Slide 2:
  Дата отчета: $дата      ← INSERT: вставка
  Выручка: $выручка       ← INSERT: вставка
```

**API запрос:**
```json
{
  "placeholders": [
    {
      "key": "заголовок",
      "type": "TEXT",
      "value": "Финансовый отчет Q4 2024"
    },
    {
      "key": "дата",
      "type": "INSERT",
      "value": "31.12.2024"
    },
    {
      "key": "выручка",
      "type": "INSERT",
      "value": "1,250,000 руб."
    }
  ]
}
```

**Результат:**
```
Slide 1:
  Финансовый отчет Q4 2024

Slide 2:
  Дата отчета: 31.12.2024
  Выручка: 1,250,000 руб.
```

## Сравнение типов плейсхолдеров

| Тип | Паттерн | Использование | Пример в шаблоне | Результат |
|-----|---------|---------------|------------------|-----------|
| **TEXT** | `{{ключ}}` | Полная замена текста | `{{цена}}` | `1000` |
| **INSERT** | `$ключ` | Вставка в существующий текст | `Цена: $цена руб.` | `Цена: 1000 руб.` |
| **IMAGE** | `{{ключ}}` | Замена на изображение | `{{лого}}` | [изображение] |

## cURL примеры

### Простая вставка
```bash
curl -X POST http://localhost:8080/api/pptx/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "templates/report.pptx",
    "placeholders": [
      {
        "key": "цена",
        "type": "INSERT",
        "value": "1000"
      }
    ]
  }' \
  --output result.pptx
```

### Множественные вставки
```bash
curl -X POST http://localhost:8080/api/pptx/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "templates/invoice.pptx",
    "placeholders": [
      {
        "key": "имя_клиента",
        "type": "INSERT",
        "value": "Иван Иванов"
      },
      {
        "key": "сумма",
        "type": "INSERT",
        "value": "50000"
      },
      {
        "key": "дата",
        "type": "INSERT",
        "value": "03.12.2024"
      }
    ]
  }' \
  --output invoice.pptx
```

## Java код

```java
@Autowired
private PptxModificationService modificationService;

public void generateInvoice() throws Exception {
    List<Placeholder> placeholders = List.of(
        // INSERT - вставка значений
        Placeholder.builder()
            .key("цена")
            .type(PlaceholderType.INSERT)
            .value("1000")
            .build(),

        Placeholder.builder()
            .key("количество")
            .type(PlaceholderType.INSERT)
            .value("5")
            .build(),

        // TEXT - полная замена
        Placeholder.builder()
            .key("заголовок")
            .type(PlaceholderType.TEXT)
            .value("Счет на оплату")
            .build()
    );

    TemplateData data = TemplateData.builder()
        .templateName("templates/invoice.pptx")
        .placeholders(placeholders)
        .build();

    XMLSlideShow presentation = modificationService.modifyPresentation(data);
}
```

## Рекомендации

### Когда использовать TEXT
- Когда нужно заменить весь блок текста
- Для заголовков и подзаголовков
- Когда формат не важен

### Когда использовать INSERT
- Когда есть статический текст с подписями
- Для форм и шаблонов документов
- Когда нужно сохранить форматирование и структуру
- Для таблиц с подписями полей

## Особенности

1. **Регистрозависимость**: `$цена` ≠ `$Цена`
2. **Множественные вхождения**: Все `$цена` в документе будут заменены
3. **Сохранение форматирования**: Стиль и форматирование текста сохраняются
4. **Производительность**: INSERT быстрее, чем TEXT (не создает новые объекты)

## Извлечение плейсхолдеров

Сервис автоматически находит оба типа плейсхолдеров:

```bash
curl "http://localhost:8080/api/pptx/placeholders?templatePath=templates/report.pptx"
```

Ответ:
```json
["заголовок", "цена", "дата", "лого"]
```

Примечание: API возвращает только ключи без символов `{{}}` и `$`.

## Расширение

Если вам нужен другой паттерн (например, `%ключ%` или `#ключ#`), просто создайте новый replacer:

```java
@Component
public class CustomPatternReplacer implements PlaceholderReplacer {
    @Override
    public boolean canHandle(Placeholder placeholder) {
        return placeholder.getType() == PlaceholderType.CUSTOM;
    }

    @Override
    public void replace(XMLSlideShow presentation, Placeholder placeholder) {
        String pattern = "%" + placeholder.getKey() + "%";
        // Логика замены
    }
}
```