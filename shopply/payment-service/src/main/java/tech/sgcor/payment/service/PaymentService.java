package tech.sgcor.payment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.sgcor.payment.dto.PaymentDto;
import tech.sgcor.payment.dto.PaymentRequest;
import tech.sgcor.payment.model.Payment;
import tech.sgcor.payment.model.PaymentStatus;
import tech.sgcor.payment.repository.PaymentRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final static String ORDER_SERVICE_URL="http://order-service:8083/api/orders/";
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    @Value("${stipe.secret-key}")
    private String stripeSecretKey;

    public PaymentDto processPayment(Long orderId, PaymentRequest request) throws StripeException {
        // Integrate with payment gateway
        Stripe.apiKey = stripeSecretKey;

        Map<String, Object> params = new HashMap<>();
        params.put("amount", (int) (request.getAmount() * 100)); //amount in cents
        params.put("currency", request.getCurrency());
        params.put("description", request.getDescription());
        params.put("source", request.getToken());

        Charge charge = Charge.create(params);
        boolean paymentSuccess = "succeeded".equals(charge.getStatus());

        if (paymentSuccess) {
            updateOrderStatus("processing", orderId);
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(request.getAmount());
        payment.setStatus(paymentSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setPaymentDate(new Date());

        paymentRepository.save(payment);

        return PaymentDto
                .builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .orderId(payment.getOrderId())
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    public void updateOrderStatus(String status, Long orderId) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(status, header);

        String updateOrderStatusUrl = ORDER_SERVICE_URL + orderId + "/update-status?status=" + status;

        restTemplate.exchange(updateOrderStatusUrl, HttpMethod.PUT, requestEntity, Void.class);
    }
}
