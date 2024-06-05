package tech.sgcor.payment.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private Long orderId;
    private Double amount;
    private PaymentStatus status;
    private Date paymentDate;
}
