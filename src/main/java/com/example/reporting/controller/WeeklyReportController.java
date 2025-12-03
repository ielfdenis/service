package com.example.reporting.controller;

import com.example.reporting.service.report.WeeklyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reports/weekly")
@RequiredArgsConstructor
public class WeeklyReportController {

    private final WeeklyReportService weeklyReportService;

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateWeeklyReport(@RequestBody Map<String, Object> data) {
        try {
            return weeklyReportService.generateWeeklyReport(data);
        } catch (Exception e) {
            log.error("Error generating weekly report", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}