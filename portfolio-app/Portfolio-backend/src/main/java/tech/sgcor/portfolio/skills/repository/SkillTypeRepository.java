package tech.sgcor.portfolio.skills.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.skills.entity.SkillType;

import java.util.List;

public interface SkillTypeRepository extends JpaRepository<SkillType, Long> {
    List<SkillType> findByUserId(Long userId);
}
