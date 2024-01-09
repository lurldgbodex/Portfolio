package tech.sgcor.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "You need to provide name of product")
    private String name;
    @NotBlank(message = "You need to provide description of product")
    private String description;
    @NotNull(message = "You need to provide price of product")
    private BigDecimal price;
    @NotBlank(message = "You need to provide category of product")
    private String category_id;
    @NotNull(message = "You need to provide the quantity")
    @Min(value = 0, message = "value cannot be below 0")
    private Integer quantity;
    @NotBlank(message = "You need to provide image_data in base 64")
    private String image_data;
}
