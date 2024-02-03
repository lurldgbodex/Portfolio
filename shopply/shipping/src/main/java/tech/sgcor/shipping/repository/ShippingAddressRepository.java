package tech.sgcor.shipping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.shipping.model.ShippingAddress;

import java.util.List;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    List<ShippingAddress> findByUserId(String userId);
}
