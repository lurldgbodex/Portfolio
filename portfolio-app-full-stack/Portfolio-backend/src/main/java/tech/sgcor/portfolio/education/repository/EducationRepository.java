package tech.sgcor.portfolio.education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.education.entity.Education;

import java.util.List;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findByUserId(Long userId);
}
