package tech.sgcor.analytics.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.analytics.dto.SalesReportDto;
import tech.sgcor.analytics.service.AnalyticsService;

import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping("/sales-reports")
    @ResponseStatus(HttpStatus.OK)
    public List<SalesReportDto> getSalesReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start_date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end_date) {
        return analyticsService.getSalesReportsByDateRange(start_date, end_date);
    }

    @PostMapping("/generate-sales-report")
    @ResponseStatus(HttpStatus.OK)
    public SalesReportDto generateSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date report_date,
            @RequestParam Double total_sales) {
     return analyticsService.generateSalesReport(report_date, total_sales);
    }
}
