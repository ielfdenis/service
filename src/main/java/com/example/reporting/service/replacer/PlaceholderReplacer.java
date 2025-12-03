package com.example.reporting.service.replacer;

import com.example.reporting.model.Placeholder;
import org.apache.poi.xslf.usermodel.XMLSlideShow;

public interface PlaceholderReplacer {
    boolean canHandle(Placeholder placeholder);
    void replace(XMLSlideShow presentation, Placeholder placeholder) throws Exception;
}