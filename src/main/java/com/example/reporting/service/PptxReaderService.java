package com.example.reporting.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PptxReaderService {

    private final ResourceLoader resourceLoader;

    public PptxReaderService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public XMLSlideShow loadTemplate(String templatePath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + templatePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return new XMLSlideShow(inputStream);
        }
    }

    public List<String> extractPlaceholders(XMLSlideShow presentation) {
        List<String> placeholders = new ArrayList<>();

        for (XSLFSlide slide : presentation.getSlides()) {
            for (XSLFShape shape : slide.getShapes()) {
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    String text = textShape.getText();

                    if (text != null && text.contains("{{") && text.contains("}}")) {
                        int start = 0;
                        while ((start = text.indexOf("{{", start)) != -1) {
                            int end = text.indexOf("}}", start);
                            if (end != -1) {
                                String placeholder = text.substring(start + 2, end).trim();
                                if (!placeholders.contains(placeholder)) {
                                    placeholders.add(placeholder);
                                }
                                start = end + 2;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }

        log.info("Found {} placeholders in template", placeholders.size());
        return placeholders;
    }
}