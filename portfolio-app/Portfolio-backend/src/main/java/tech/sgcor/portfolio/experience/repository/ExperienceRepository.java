package tech.sgcor.portfolio.experience.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.experience.entity.Experience;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByUserId(Long userId);
}