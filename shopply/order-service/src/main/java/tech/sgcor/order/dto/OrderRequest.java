package tech.sgcor.order.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotBlank(message = "user_id is required")
    private String user_id;
    private List<OrderItemDto> order_items;
}
