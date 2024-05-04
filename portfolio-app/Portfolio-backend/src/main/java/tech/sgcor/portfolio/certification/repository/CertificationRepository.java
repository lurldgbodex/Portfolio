package tech.sgcor.portfolio.certification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.certification.entity.Certification;

import java.util.List;
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByUserId(Long userId);
}
