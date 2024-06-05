package tech.sgcor.inventory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.inventory.service.InventoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/check-availability/{productId}?quantity={quantity}")
    public boolean isProductInStock(@PathVariable(name = "productId") String id,
                                    @RequestParam(name = "quantity") int quantity) {
        return inventoryService.isProductInStock(id, quantity);
    }

    @PutMapping("/update?productId={productId}&quantity={quantity}")
    public void updateInventoryAfterOrder(@RequestParam(name = "productId") String id,
                                          @RequestParam(name = "quantity") int quantity) {
        inventoryService.updateInventory(id, quantity);
    }

    @PutMapping("/update/{productId}?quantity={quantity}")
    public void createOrUpdateInventory(@PathVariable(name="productId") String id,
                                        @RequestParam(name="quantity") int quantity) {
        inventoryService.createOrUpdateInventory(id, quantity);
    }
}
