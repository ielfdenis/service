package com.example.reporting.controller;

import com.example.reporting.model.TemplateData;
import com.example.reporting.service.PptxDownloadService;
import com.example.reporting.service.PptxModificationService;
import com.example.reporting.service.PptxReaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/pptx")
@RequiredArgsConstructor
public class PptxController {

    private final PptxReaderService pptxReaderService;
    private final PptxModificationService pptxModificationService;
    private final PptxDownloadService pptxDownloadService;

    @GetMapping("/placeholders")
    public ResponseEntity<List<String>> getPlaceholders(@RequestParam String templatePath) {
        try {
            XMLSlideShow presentation = pptxReaderService.loadTemplate(templatePath);
            List<String> placeholders = pptxReaderService.extractPlaceholders(presentation);
            presentation.close();
            return ResponseEntity.ok(placeholders);
        } catch (Exception e) {
            log.error("Error extracting placeholders from template: {}", templatePath, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<Resource> generatePresentation(@RequestBody TemplateData templateData) {
        try {
            XMLSlideShow modifiedPresentation = pptxModificationService.modifyPresentation(templateData);

            String filename = templateData.getTemplateName().replace(".pptx", "") + "_generated.pptx";
            ResponseEntity<Resource> response = pptxDownloadService.prepareDownload(modifiedPresentation, filename);

            modifiedPresentation.close();
            return response;
        } catch (Exception e) {
            log.error("Error generating presentation from template: {}", templateData.getTemplateName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/modify")
    public ResponseEntity<byte[]> modifyPresentation(@RequestBody TemplateData templateData) {
        try {
            XMLSlideShow modifiedPresentation = pptxModificationService.modifyPresentation(templateData);
            byte[] bytes = pptxDownloadService.convertToBytes(modifiedPresentation);
            modifiedPresentation.close();
            return ResponseEntity.ok(bytes);
        } catch (Exception e) {
            log.error("Error modifying presentation: {}", templateData.getTemplateName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}