package tech.sgcor.cart.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {
    @NotBlank(message = "productId must be provided")
    private String productId;
    @NotBlank(message = "ProductName must be provided")
    private String productName;
    @NotNull(message = "price must be provided")
    private Double price;
    @NotNull(message = "quantity must be provided")
    private Integer quantity;
}
