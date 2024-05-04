package tech.sgcor.portfolio.skills;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillTypeRepository extends JpaRepository<SkillType, Long> {
    List<SkillType> findByUserId(Long userId);
}
