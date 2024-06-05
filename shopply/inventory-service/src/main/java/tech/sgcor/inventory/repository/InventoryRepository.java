package tech.sgcor.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.sgcor.inventory.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory findByProductId(String productId);
}
