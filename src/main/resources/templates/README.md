# PPTX Templates Directory

Place your PowerPoint (PPTX) template files in this directory.

## Template Guidelines

1. **Text Placeholders**: Use double curly braces `{{placeholderName}}`
   - Example: `{{companyName}}`, `{{reportTitle}}`, `{{date}}`

2. **Image Placeholders**: Create a text box with `{{imageName}}`
   - The text box will be replaced with the actual image
   - Size and position the text box to match your desired image dimensions

## Example Template Structure

```
Slide 1:
- Title: "{{reportTitle}}"
- Subtitle: "Prepared for {{companyName}}"
- Logo: Text box containing "{{logo}}"

Slide 2:
- Header: "{{sectionTitle}}"
- Body text with placeholders: "This report covers {{topic}} for Q{{quarter}} {{year}}"
- Chart: Text box containing "{{chartImage}}"
```

## Accessing Templates in Code

Templates are loaded using their path relative to this directory:

```java
String templatePath = "templates/your-template.pptx";
```

## Tips

- Keep template names descriptive
- Document required placeholders for each template
- Test templates with sample data before production use
- Maintain backup copies of original templates