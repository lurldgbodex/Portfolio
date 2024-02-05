package tech.sgcor.analytics.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.sgcor.analytics.dto.SalesReportDto;
import tech.sgcor.analytics.model.SalesReport;
import tech.sgcor.analytics.repository.SalesReportRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final SalesReportRepository salesReportRepository;

    public List<SalesReportDto> getSalesReportsByDateRange(Date startDate, Date endDate) {
        List<SalesReport> salesReports = salesReportRepository.findByReportDateBetween(startDate, endDate);

        return salesReports.stream().map(report -> SalesReportDto
                        .builder()
                        .id(report.getId())
                        .reportDate(report.getReportDate())
                        .totalSales(report.getTotalSales())
                        .build()).toList();
    }

    public SalesReportDto generateSalesReport(Date reportDate, Double totalSales) {
        SalesReport salesReport = new SalesReport();
        salesReport.setReportDate(reportDate);
        salesReport.setTotalSales(totalSales);

        salesReport = salesReportRepository.save(salesReport);

        return SalesReportDto.builder()
                .id(salesReport.getId())
                .reportDate(salesReport.getReportDate())
                .totalSales(salesReport.getTotalSales())
                .build();
    }
}
