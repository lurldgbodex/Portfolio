package tech.sgcor.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.sgcor.order.model.OrderItem;
import tech.sgcor.order.model.OrderStatus;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private String userId;
    private List<OrderItem> orderItems;
    private Date orderDate;
    private OrderStatus orderStatus;
}
