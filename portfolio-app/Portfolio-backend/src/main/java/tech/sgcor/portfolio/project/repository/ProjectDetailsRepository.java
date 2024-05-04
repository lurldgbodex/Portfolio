package tech.sgcor.portfolio.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.project.entity.ProjectDetails;

public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Long> {
}
