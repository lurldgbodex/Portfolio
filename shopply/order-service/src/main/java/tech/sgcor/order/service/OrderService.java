package tech.sgcor.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.order.dto.*;
import tech.sgcor.order.exception.BadRequestException;
import tech.sgcor.order.exception.OrderNotFoundException;
import tech.sgcor.order.exception.ProductNotAvailableException;
import tech.sgcor.order.model.Order;
import tech.sgcor.order.model.OrderItem;
import tech.sgcor.order.model.OrderStatus;
import tech.sgcor.order.repository.OrderRepository;

import java.net.URI;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final String INVENTORY_SERVICE_URL = "http://inventory-service:8081/api/inventory";

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public CreateOrderResponse createOrder(OrderRequest request) {
        //todo: implement and call inventory service to check if product is in stock before order creation
        checkProductAvailability(request.getOrder_items());

        // Create order
        Order order = new Order();
        order.setUserId(request.getUser_id());
        order.setOrderItems(request.getOrder_items().stream()
                .map(items-> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProductId(items.getProduct_id());
                    orderItem.setProductName(items.getProduct_name());
                    orderItem.setPrice(items.getPrice());
                    orderItem.setQuantity(items.getQuantity());
                    return orderItem;
                }).toList());
        order.setOrderDate(new Date());
        order.setOrderStatus(OrderStatus.CREATED);

        // save order
        var saveOrder = orderRepository.save(order);

        // update inventory
        updateInventory(request.getOrder_items());

        // return created order location and successful creation message
        String path = "/api/orders/".concat(saveOrder.getUserId());
        URI createdOrderLocation = UriComponentsBuilder.fromPath(path).build().toUri();
        CustomResponse response = new CustomResponse(201, "order created successfully");

        return CreateOrderResponse
                .builder()
                .location(createdOrderLocation)
                .message(response)
                .build();
    }

    public List<OrderDto> getOrderHistory(String userId) {
        List<Order> order = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return order.stream()
                .map(items -> {
                    OrderDto orders = new OrderDto();
                    orders.setId(items.getId());
                    orders.setUserId(items.getUserId());
                    orders.setOrderDate(items.getOrderDate());
                    orders.setOrderStatus(items.getOrderStatus());
                    orders.setOrderItems(items.getOrderItems());
                    return orders;
                }).toList();
    }

    public OrderDto getOrderById(Long orderId) {
        Order order = findOrderById(orderId);

        return OrderDto
                .builder()
                .id(order.getId())
                .userId(order.getUserId())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .orderItems(order.getOrderItems())
                .build();
    }

    public CustomResponse updateOrderStatus(Long orderId, String status) {
        OrderStatus orderStatus;

        try {
            orderStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException("invalid order status");
        }
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        return new CustomResponse(200, "order status successfully update");
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(()-> new OrderNotFoundException("order not found for id"));
    }

    private void checkProductAvailability(List<OrderItemDto> orderItems) {
        for (OrderItemDto orderItem: orderItems) {
            String productAvailabilityUrl = INVENTORY_SERVICE_URL + "/check-availability/"
                    + orderItem.getProduct_id() + "?quantity=" + orderItem.getQuantity();
            Boolean isAvailable = restTemplate.getForObject(productAvailabilityUrl, Boolean.class);

            if (isAvailable == null || !isAvailable) {
                throw new ProductNotAvailableException("product not available in stock");
            }
        }
    }

    private void updateInventory(List<OrderItemDto> orderItems) {
        for (OrderItemDto orderItem : orderItems) {
            String updateInventoryUrl = INVENTORY_SERVICE_URL + "/update?productId="
                    + orderItem.getProduct_id() + "&quantity=" + orderItem.getQuantity();
            restTemplate.postForObject(updateInventoryUrl, null, void.class);
        }
    }
}
