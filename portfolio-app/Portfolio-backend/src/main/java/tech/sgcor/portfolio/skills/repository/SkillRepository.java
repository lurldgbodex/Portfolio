package tech.sgcor.portfolio.skills.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.skills.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}

