package com.example.reporting.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class PptxDownloadService {

    public ResponseEntity<Resource> prepareDownload(XMLSlideShow presentation, String filename) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        presentation.write(outputStream);
        byte[] bytes = outputStream.toByteArray();

        ByteArrayResource resource = new ByteArrayResource(bytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        log.info("Prepared PPTX file for download: {}, size: {} bytes", filename, bytes.length);

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(bytes.length)
            .body(resource);
    }

    public byte[] convertToBytes(XMLSlideShow presentation) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        presentation.write(outputStream);
        return outputStream.toByteArray();
    }
}