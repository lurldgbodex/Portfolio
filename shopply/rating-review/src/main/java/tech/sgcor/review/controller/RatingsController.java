package tech.sgcor.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.review.dto.AddReviewRequest;
import tech.sgcor.review.dto.RatingDto;
import tech.sgcor.review.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ratings-reviews")
public class RatingsController {
    private final RatingService ratingService;

    @GetMapping("/product/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RatingDto> getReviewsByProduct(@PathVariable(name = "productId") String id) {
        return ratingService.getReviewsByProduct(id);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public RatingDto addRatingReview(@RequestBody @Valid AddReviewRequest request) {
        return ratingService.addRatingReview(request);
    }
}
