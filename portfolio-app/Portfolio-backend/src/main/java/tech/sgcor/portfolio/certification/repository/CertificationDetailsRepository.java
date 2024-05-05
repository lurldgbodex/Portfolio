package tech.sgcor.portfolio.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.certification.entity.CertificationDetails;

public interface CertificationDetailsRepository extends JpaRepository<CertificationDetails, Long> {
}
