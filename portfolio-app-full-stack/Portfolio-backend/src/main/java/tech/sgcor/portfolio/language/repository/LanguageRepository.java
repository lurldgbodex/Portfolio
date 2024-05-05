package tech.sgcor.portfolio.language.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.language.entity.Language;

import java.util.List;


public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language>findByUserId(Long userId);
}
