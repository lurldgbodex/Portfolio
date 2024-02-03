package tech.sgcor.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.review.model.Rating;

import java.util.List;

public interface RatingsRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(String productId);
}
