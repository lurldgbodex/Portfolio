package tech.sgcor.portfolio.project;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.project.entity.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUserId(Long userId);
}
