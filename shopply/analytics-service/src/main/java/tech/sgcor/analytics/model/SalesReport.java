package tech.sgcor.analytics.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "sales_reports")
public class SalesReport {
    @Id
    private String id;
    private Date reportDate;
    private Double totalSales;
}
