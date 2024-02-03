package tech.sgcor.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.sgcor.review.dto.AddReviewRequest;
import tech.sgcor.review.dto.RatingDto;
import tech.sgcor.review.model.Rating;
import tech.sgcor.review.repository.RatingsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingsRepository ratingsRepository;

    public List<RatingDto> getReviewsByProduct(String productId) {
        List<Rating> rating = ratingsRepository.findByProductId(productId);

        return rating.stream()
                .map(reviews -> RatingDto
                        .builder()
                        .id(reviews.getId())
                        .product_id(reviews.getProductId())
                        .user_id(reviews.getUserId())
                        .rating(reviews.getRating())
                        .review(reviews.getReview())
                        .build()).toList();
    }

    public RatingDto addRatingReview(AddReviewRequest request) {
        Rating ratingReview = new Rating();
        ratingReview.setProductId(request.getProduct_id());
        ratingReview.setUserId(request.getUser_id());
        ratingReview.setRating(request.getRating());
        ratingReview.setReview(request.getReview());

        ratingReview = ratingsRepository.save(ratingReview);
        return RatingDto
                .builder()
                .id(ratingReview.getId())
                .product_id(ratingReview.getProductId())
                .user_id(ratingReview.getUserId())
                .rating(ratingReview.getRating())
                .review(ratingReview.getReview())
                .build();
    }
}
