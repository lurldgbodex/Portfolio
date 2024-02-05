package tech.sgcor.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.dto.ProductRequest;
import tech.sgcor.product.dto.ProductResponse;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;

    @PostMapping("/create")
    public ResponseEntity<CustomResponse> createProduct(@RequestBody @Valid ProductRequest request) {
        return ResponseEntity
                .created(service.createProduct(request))
                .body(new CustomResponse(201,"product created successfully"));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponse getProduct(@PathVariable(name = "id") String productId) {
        return service.getProductById(productId);
    }

    @GetMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getProductByCategory(@PathVariable(name = "categoryId") String id) {
        return service.getProductsByCategory(id);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> searchProducts(@RequestParam(name = "keyword") String keyword) {
        return service.searchProducts(keyword);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return service.getAllProducts();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse updateProduct(
            @PathVariable(name = "id") String productId, @RequestBody UpdateProduct request) {
        return service.updateProduct(productId, request);
    }

    @DeleteMapping("/{id}")
    public CustomResponse deleteProduct(@PathVariable(name = "id") String productId) {
        return service.deleteProduct(productId);
    }
}
