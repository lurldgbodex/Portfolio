package tech.sgcor.order.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemDto {
    @NotBlank(message = "product_id is required")
    private String product_id;
    @NotBlank(message = "product_name is required")
    private String product_name;
    @NotNull(message = "price is required")
    private Double price;
    @NotNull(message = "quantity of product is required")
    private Integer quantity;
}
