package tech.sgcor.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String token;
    private Double amount;
    private String currency;
    private String description;
}
