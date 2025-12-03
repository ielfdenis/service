# PPTX Template Processing - Usage Guide

## Overview

This application provides a flexible and extensible system for processing PowerPoint (PPTX) templates. You can replace text and images in templates using placeholders.

## Architecture

### Core Components

1. **Models** (`com.example.reporting.model`)
   - `Placeholder` - Represents a placeholder with key, type, and value
   - `PlaceholderType` - Enum for TEXT and IMAGE types
   - `TemplateData` - Contains template name and list of placeholders
   - `ImageData` - Encapsulates image bytes and metadata

2. **Services**
   - `PptxReaderService` - Loads templates and extracts placeholders
   - `PptxModificationService` - Coordinates placeholder replacement
   - `PptxDownloadService` - Prepares presentations for download

3. **Replacers** (`com.example.reporting.service.replacer`)
   - `PlaceholderReplacer` - Interface for extensibility
   - `TextPlaceholderReplacer` - Replaces text placeholders
   - `ImagePlaceholderReplacer` - Replaces image placeholders

## Template Format

Use double curly braces for placeholders in your PPTX templates:

- Text placeholder: `{{companyName}}`
- Image placeholder: `{{logo}}` (in a text box)

## API Endpoints

### 1. Get Placeholders from Template

```http
GET /api/pptx/placeholders?templatePath=templates/report-template.pptx
```

Response:
```json
["companyName", "reportTitle", "logo", "chartImage"]
```

### 2. Generate Presentation (Download)

```http
POST /api/pptx/generate
Content-Type: application/json

{
  "templateName": "templates/report-template.pptx",
  "placeholders": [
    {
      "key": "companyName",
      "type": "TEXT",
      "value": "Acme Corporation"
    },
    {
      "key": "reportTitle",
      "type": "TEXT",
      "value": "Q4 2024 Financial Report"
    },
    {
      "key": "logo",
      "type": "IMAGE",
      "value": {
        "imageBytes": "base64_encoded_bytes",
        "contentType": "image/png",
        "pictureType": 6
      }
    }
  ]
}
```

Returns the modified PPTX file for download.

### 3. Modify Presentation (Get Bytes)

```http
POST /api/pptx/modify
Content-Type: application/json

{
  "templateName": "templates/report-template.pptx",
  "placeholders": [...]
}
```

Returns the modified presentation as byte array.

## Usage Examples

### Java Example

```java
@Autowired
private PptxModificationService modificationService;

@Autowired
private PptxDownloadService downloadService;

public void generateReport() throws Exception {
    // Create placeholders
    List<Placeholder> placeholders = List.of(
        Placeholder.builder()
            .key("companyName")
            .type(PlaceholderType.TEXT)
            .value("Acme Corp")
            .build(),
        Placeholder.builder()
            .key("logo")
            .type(PlaceholderType.IMAGE)
            .value(ImageUtils.loadImageFromFile(Path.of("logo.png")))
            .build()
    );

    // Create template data
    TemplateData templateData = TemplateData.builder()
        .templateName("templates/report.pptx")
        .placeholders(placeholders)
        .build();

    // Modify presentation
    XMLSlideShow presentation = modificationService.modifyPresentation(templateData);

    // Prepare download
    ResponseEntity<Resource> response = downloadService.prepareDownload(
        presentation,
        "report_final.pptx"
    );
}
```

### cURL Example

```bash
# Get placeholders
curl -X GET "http://localhost:8080/api/pptx/placeholders?templatePath=templates/report.pptx"

# Generate presentation
curl -X POST http://localhost:8080/api/pptx/generate \
  -H "Content-Type: application/json" \
  -d '{
    "templateName": "templates/report.pptx",
    "placeholders": [
      {
        "key": "title",
        "type": "TEXT",
        "value": "Annual Report 2024"
      }
    ]
  }' \
  --output report.pptx
```

## Extending the System

### Adding a New Placeholder Type

1. Add the type to `PlaceholderType` enum:
```java
public enum PlaceholderType {
    TEXT,
    IMAGE,
    TABLE  // New type
}
```

2. Create a new replacer implementing `PlaceholderReplacer`:
```java
@Component
public class TablePlaceholderReplacer implements PlaceholderReplacer {

    @Override
    public boolean canHandle(Placeholder placeholder) {
        return placeholder.getType() == PlaceholderType.TABLE;
    }

    @Override
    public void replace(XMLSlideShow presentation, Placeholder placeholder) {
        // Implementation for table replacement
    }
}
```

3. Spring will automatically register and use the new replacer.

## Template Setup

1. Create your PPTX template in PowerPoint
2. Add text boxes with placeholders like `{{placeholderName}}`
3. Save the template in `src/main/resources/templates/`
4. Reference it using the path: `templates/your-template.pptx`

## Image Guidelines

- Supported formats: PNG, JPEG, GIF, BMP, TIFF
- Image placeholder should be in a text box
- The image will replace the text box and maintain its position and size
- For best results, size the text box to match your desired image dimensions

## Error Handling

The system logs warnings for:
- Missing placeholders in template
- Unsupported placeholder types
- I/O errors during processing

Check logs for detailed error messages during development.

## Notes

- Templates are loaded from classpath resources
- All modifications are done in-memory
- Original templates are never modified
- Supports multiple placeholders of the same type
- Case-sensitive placeholder matching