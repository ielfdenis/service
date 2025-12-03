package com.example.reporting.service;

import com.example.reporting.model.Placeholder;
import com.example.reporting.model.TemplateData;
import com.example.reporting.service.replacer.PlaceholderReplacer;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PptxModificationService {

    private final List<PlaceholderReplacer> replacers;
    private final PptxReaderService pptxReaderService;

    public PptxModificationService(List<PlaceholderReplacer> replacers, PptxReaderService pptxReaderService) {
        this.replacers = replacers;
        this.pptxReaderService = pptxReaderService;
    }

    public XMLSlideShow modifyPresentation(TemplateData templateData) throws Exception {
        XMLSlideShow presentation = pptxReaderService.loadTemplate(templateData.getTemplateName());

        for (Placeholder placeholder : templateData.getPlaceholders()) {
            PlaceholderReplacer replacer = findReplacer(placeholder);
            if (replacer != null) {
                replacer.replace(presentation, placeholder);
            } else {
                log.warn("No replacer found for placeholder: {} of type: {}",
                    placeholder.getKey(), placeholder.getType());
            }
        }

        log.info("Successfully modified presentation with {} placeholders",
            templateData.getPlaceholders().size());
        return presentation;
    }

    private PlaceholderReplacer findReplacer(Placeholder placeholder) {
        return replacers.stream()
            .filter(replacer -> replacer.canHandle(placeholder))
            .findFirst()
            .orElse(null);
    }
}