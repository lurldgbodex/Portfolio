package tech.sgcor.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewRequest {
    @NotBlank(message = "product_id is required")
    private String product_id;
    @NotBlank(message = "user_id is required")
    private String user_id;
    @NotNull(message = "rating is required")
    @Min(value = 1, message= "value cannot be less than 1")
    @Max(value = 5, message = "value cannot be greater than 5")
    private Integer rating;
    @NotBlank(message = "review is required")
    private String review;
}
