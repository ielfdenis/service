package com.example.reporting.service.replacer;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.PlaceholderType;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InsertPlaceholderReplacer implements PlaceholderReplacer {

    @Override
    public boolean canHandle(Placeholder placeholder) {
        return placeholder.getType() == PlaceholderType.INSERT;
    }

    @Override
    public void replace(XMLSlideShow presentation, Placeholder placeholder) {
        String searchPattern = "$" + placeholder.getKey();
        String replacement = String.valueOf(placeholder.getValue());

        int replacementCount = 0;

        for (XSLFSlide slide : presentation.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    replacementCount += replaceInShape(textShape, searchPattern, replacement);
                }
            }
        }

        log.info("Inserted value for placeholder: {} -> {} ({} occurrences)",
            placeholder.getKey(), replacement, replacementCount);
    }

    private int replaceInShape(XSLFTextShape textShape, String searchPattern, String replacement) {
        int count = 0;

        for (XSLFTextParagraph paragraph : textShape.getTextParagraphs()) {
            for (XSLFTextRun run : paragraph.getTextRuns()) {
                String text = run.getRawText();
                if (text != null && text.contains(searchPattern)) {
                    String newText = text.replace(searchPattern, replacement);
                    run.setText(newText);
                    count++;
                }
            }
        }

        return count;
    }
}