package tech.sgcor.review.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingDto {
    private Long id;
    private String product_id;
    private String user_id;
    private int rating;
    private String review;
}
