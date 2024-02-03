package tech.sgcor.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.order.dto.CustomResponse;
import tech.sgcor.order.dto.OrderDto;
import tech.sgcor.order.dto.OrderRequest;
import tech.sgcor.order.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<CustomResponse> createOrder(@RequestBody @Valid OrderRequest request) {
        var res = orderService.createOrder(request);
        return ResponseEntity
                .created(res.getLocation())
                .body(res.getMessage());
    }

    @GetMapping("{userId}/history")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderDto> getOrderHistory(@PathVariable(name = "userId") String id) {
        return orderService.getOrderHistory(id);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderDto getOrderById(@PathVariable(name = "orderId") Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{orderId}/update-status")
    public CustomResponse updateOrderStatus(@PathVariable(name = "orderId") Long id,
                                            @RequestParam String status) {
        return orderService.updateOrderStatus(id, status);
    }
}
