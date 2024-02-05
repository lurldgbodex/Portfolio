package tech.sgcor.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.order.model.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(String userId);
}
