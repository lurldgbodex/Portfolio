package tech.sgcor.portfolio.about;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AboutRepository extends JpaRepository<About, Long> {
    About findAboutWithUserById(Long aboutId);
}
