package tech.sgcor.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Data
@Builder
public class CreateOrderResponse {
    private URI location;
    private CustomResponse message;
}
