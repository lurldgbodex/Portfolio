package tech.sgcor.analytics.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class SalesReportDto {
    private String id;
    private Date reportDate;
    private Double totalSales;
}
