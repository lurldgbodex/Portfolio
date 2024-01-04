package tech.sgcor.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.dto.ProductRequest;
import tech.sgcor.product.dto.ProductResponse;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.exception.BadRequestException;
import tech.sgcor.product.exception.ProductNotFoundException;
import tech.sgcor.product.model.Product;
import tech.sgcor.product.repository.ProductRepository;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public URI createProduct(ProductRequest request) {
        Product product = Product
                .builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .build();
        repository.save(product);

        log.info("product with id {} created successfully", product.getId());

        return UriComponentsBuilder.fromPath("/api/products/{id}").buildAndExpand(product.getId()).toUri();
    }

    public ProductResponse getProduct(String id) {
        Product product = repository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException("product not found with id"));

        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = repository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public CustomResponse updateProduct(String id, UpdateProduct request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("product not found"));

        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getCategory(), ""),
                Objects.toString(request.getDescription(), ""),
                Objects.toString(request.getPrice(), "")
        ).allMatch(String::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("you need to provide the field you want to update");
        }

        product.setName(request.getName() == null ? product.getName() : request.getName());
        product.setCategory(request.getCategory() == null ? product.getCategory() : request.getCategory());
        product.setDescription(request.getDescription() == null ? product.getDescription() : request.getDescription());
        product.setPrice(request.getPrice() == null ? product.getPrice() : request.getPrice());

        repository.save(product);

        return new CustomResponse(200, "product updated successfully");
    }

    public CustomResponse deleteProduct(String id) {
        Product product = repository.findById(id)
                .orElseThrow(()->new ProductNotFoundException("product with id not found"));

        repository.delete(product);
        return new CustomResponse(200, "product deleted successfully");
    }
}
