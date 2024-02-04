package tech.sgcor.analytics.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.sgcor.analytics.model.SalesReport;

import java.util.Date;
import java.util.List;

public interface SalesReportRepository extends MongoRepository<SalesReport, String> {
    List<SalesReport> findByReportDateBetween(Date startDate, Date endDate);
}
