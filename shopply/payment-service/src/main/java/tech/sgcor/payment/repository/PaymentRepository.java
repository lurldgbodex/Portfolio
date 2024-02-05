package tech.sgcor.payment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tech.sgcor.payment.model.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String> {
}
