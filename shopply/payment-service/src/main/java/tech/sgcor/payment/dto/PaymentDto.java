package tech.sgcor.payment.dto;

import lombok.Builder;
import lombok.Data;
import tech.sgcor.payment.model.PaymentStatus;

import java.util.Date;

@Data
@Builder
public class PaymentDto {
    private String id;
    private Long orderId;
    private Double amount;
    private PaymentStatus status;
    private Date paymentDate;
}
