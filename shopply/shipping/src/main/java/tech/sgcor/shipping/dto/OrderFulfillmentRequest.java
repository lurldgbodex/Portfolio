package tech.sgcor.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderFulfillmentRequest {
    @NotBlank(message = "order_id is required")
    private String order_id;
    @NotNull(message = "shipping_address_id is required")
    private Long shipping_address_id;
}
