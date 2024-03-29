package tech.sgcor.portfolio.language;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language>findByUserId(Long userId);
}
