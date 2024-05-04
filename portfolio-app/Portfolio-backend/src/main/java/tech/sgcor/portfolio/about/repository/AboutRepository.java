package tech.sgcor.portfolio.about.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.portfolio.about.entity.About;

import java.util.Optional;

public interface AboutRepository extends JpaRepository<About, Long> {
    Optional<About> findAboutWithUserById(Long userId);
}
