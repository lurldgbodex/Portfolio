package tech.sgcor.shipping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.sgcor.shipping.dto.OrderFulfillmentRequest;

@Service
@RequiredArgsConstructor
public class FulfillmentService {
    private final ShippingService shippingService;

    public void fulfillOrder(String userId, OrderFulfillmentRequest request) {
        //todo: implement fulfillment logic
        //todo: update order status
        //todo: send notification
    }
}
