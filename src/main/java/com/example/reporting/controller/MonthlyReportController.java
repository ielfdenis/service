package com.example.reporting.controller;

import com.example.reporting.service.report.MonthlyReportService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@Slf4j
@RestController
@RequestMapping("/api/reports/monthly")
@RequiredArgsConstructor
public class MonthlyReportController {

    private final MonthlyReportService monthlyReportService;

    @PostMapping("/generate")
    public ResponseEntity<Resource> generateMonthlyReport(@RequestBody MonthlyReportRequest request) {
        try {
            return monthlyReportService.generateMonthlyReport(
                request.getMonth(),
                request.getYear(),
                request.getCompanyName(),
                request.getRevenue(),
                request.getExpenses(),
                request.getProfit(),
                request.getLogoPath() != null ? Path.of(request.getLogoPath()) : null,
                request.getRevenueChartPath() != null ? Path.of(request.getRevenueChartPath()) : null,
                request.getExpensesChartPath() != null ? Path.of(request.getExpensesChartPath()) : null
            );
        } catch (Exception e) {
            log.error("Error generating monthly report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Data
    public static class MonthlyReportRequest {
        private String month;
        private String year;
        private String companyName;
        private String revenue;
        private String expenses;
        private String profit;
        private String logoPath;
        private String revenueChartPath;
        private String expensesChartPath;
    }
}