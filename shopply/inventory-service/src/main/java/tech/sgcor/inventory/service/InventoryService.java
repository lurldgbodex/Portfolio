package tech.sgcor.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.sgcor.inventory.model.Inventory;
import tech.sgcor.inventory.repository.InventoryRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public boolean isProductInStock(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);

        return inventory != null && inventory.getQuantity() >= quantity;
    }

    public void updateInventory(String productId, int quantityChange) {
        Inventory inventory = inventoryRepository.findByProductId(productId);

        if (inventory != null) {
            int updatedQuantity = inventory.getQuantity() - quantityChange;
            inventory.setQuantity(updatedQuantity);
            inventoryRepository.save(inventory);
            log.info("inventory update with quantity: " + updatedQuantity);
        }
    }

    public void createOrUpdateInventory(String productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);

        if (inventory == null) {
            Inventory newInventory = new Inventory();
            newInventory.setProductId(productId);
            newInventory.setQuantity(quantity);
            inventoryRepository.save(newInventory);

            log.info("inventory create for new product: " + productId, "quantity: " + quantity);
        } else {
            inventory.setQuantity(quantity);
            inventoryRepository.save(inventory);

            log.info("inventory updated for product: " + productId, "quantity: " + quantity);
        }
    }
}
