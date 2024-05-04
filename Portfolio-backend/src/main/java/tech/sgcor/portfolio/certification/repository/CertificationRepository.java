package tech.sgcor.portfolio.certification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface CertificationRepository extends JpaRepository<Certification, Long> {
    List<Certification> findByUserId(Long userId);
}
