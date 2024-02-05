package tech.sgcor.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.product.dto.*;
import tech.sgcor.product.exception.BadRequestException;
import tech.sgcor.product.exception.ResourceNotFoundException;
import tech.sgcor.product.model.Product;
import tech.sgcor.product.repository.ProductRepository;

import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private static final String INVENTORY_SERVICE_URL = "http://inventory-service:8081/api/inventory";
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    /**
     * description: create a new product
     *
     * @param request : request body to create product
     * @return created product uri
     */
    public URI createProduct(ProductRequest request) {
        // validate image data from request and convert it to binary.
        byte[] imageData;

        if (isValidBase64(request.getImage_data())) {
            imageData = Base64.getDecoder()
                    .decode(request.getImage_data());
        } else throw new BadRequestException("Invalid Base 64 imageData");

        Product product = Product
                .builder()
                .name(request.getName())
                .description(request.getDescription())
                .categoryId(request.getCategory_id())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .imageData(imageData)
                .build();
        productRepository.save(product);

        // create an inventory of the product
        notifyInventoryService(product.getId(), product.getQuantity());

        log.info("product with id {} created successfully", product.getId());

        return UriComponentsBuilder.fromPath("/api/products/{id}").buildAndExpand(product.getId()).toUri();
    }

    /**
     * description: get a product by id
     *
     * @param id: id of the product to get
     * @return the product with the id
     */
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("product not found with id"));

        // encode image data to base 64 before returning response
        String imageData = Base64.getEncoder().encodeToString(product.getImageData());

        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .category_id(product.getCategoryId())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .image_data(imageData)
                .build();
    }

    public List<ProductResponse> getProductsByCategory(String categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);

        return products.stream().map(this::mapToProductResponse).toList();
    }

    /**
     *
     * @return all the products in the database
     */
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }

    /**
     * description: search products containing search keyword using regex
     * 
     * @param keyword: words to search against
     * @return list of products matching search keyword
     */
    public List<ProductResponse> searchProducts(String keyword) {
        // create regex pattern to match any part of the product name containing keyword
        Pattern pattern = Pattern.compile(".*" + keyword + ".*", Pattern.CASE_INSENSITIVE);

        // find products using regex pattern
        List<Product> matchingProducts = productRepository.findByNameRegex(pattern);

        // sort products based on exact matches first
        List<Product> sortedProducts = matchingProducts.stream()
                .sorted((p1, p2) -> {
                    boolean exactMatch1 = p1.getName().equalsIgnoreCase(keyword);
                    boolean exactMatch2 = p2.getName().equalsIgnoreCase(keyword);

                    // sort by exact matches first
                    if (exactMatch1 && !exactMatch2) {
                        return -1;
                    } else if (!exactMatch1 && exactMatch2) {
                        return 1;
                    }

                    // sort by product name in a case-insensitive manner
                    return p1.getName().compareToIgnoreCase(p2.getName());
                }).toList();

        return sortedProducts.stream().map(this::mapToProductResponse).toList();
    }

    /**
     * description: helper to map the product instance to productDto
     *
     * @param product: instance of product
     * @return product instance mapped to product response
     */
    private ProductResponse mapToProductResponse(Product product) {
        // convert image data to base64 from binary
        String imageData = Base64.getEncoder().encodeToString(product.getImageData());
        return ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .category_id(product.getCategoryId())
                .description(product.getDescription())
                .price(product.getPrice())
                .image_data(imageData)
                .build();
    }

    /**
     * description: update a product, replacing provided fields
     *  and persisting non provided fields
     *
     * @param id: id of the product to update
     * @param request: request body of the fields to update
     * @return customResponse with status code 200 and successful update message
     *  if update successful
     */
    public CustomResponse updateProduct(String id, UpdateProduct request) {
        // retrieve product from database
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("product not found"));

        // check if request is blank and throw an error if all fields is blank
        boolean allFieldsBlank = Stream.of(
                Objects.toString(request.getName(), ""),
                Objects.toString(request.getCategory_id(), ""),
                Objects.toString(request.getDescription(), ""),
                Objects.toString(request.getPrice(), ""),
                Objects.toString(request.getQuantity(), ""),
                Objects.toString(request.getImage_data(), "")
        ).allMatch(String::isBlank);

        if (allFieldsBlank) {
            throw new BadRequestException("you need to provide the field you want to update");
        }

        // check if image data is part of request, valid and converted to binary before updating
        if (request.getImage_data() != null) {
            if (isValidBase64(request.getImage_data())) {
                byte[] imageDate = Base64.getDecoder()
                        .decode(request.getImage_data());
                product.setImageData(imageDate);
            } else throw new BadRequestException("Invalid Base 64 imageData");
        }

        // Validating quantity value of request
        if (request.getQuantity() != null) {
           if (request.getQuantity() >= 0) {
               product.setQuantity(request.getQuantity());
           }
        }

        // validating other request values and updating only fields provided
        product.setName(request.getName() == null ? product.getName() : request.getName());
        product.setCategoryId(request.getCategory_id() == null ? product.getCategoryId() : request.getCategory_id());
        product.setDescription(request.getDescription() == null ? product.getDescription() : request.getDescription());
        product.setPrice(request.getPrice() == null ? product.getPrice() : request.getPrice());

        productRepository.save(product);

        notifyInventoryService(product.getId(), product.getQuantity());

        return new CustomResponse(200, "product updated successfully");
    }

    /**
     * description: delete a product
     *
     * @param id: id of product to delete
     * @return CustomResponse with status 200 and success message
     */
    public CustomResponse deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("product with id not found"));

        productRepository.delete(product);
        return new CustomResponse(200, "product deleted successfully");
    }

    /**
     * description: helper method to validate base64String
     *
     * @param base64String: base64String to validate
     * @return boolean value, true if valid otherwise false
     */
    public boolean isValidBase64(String base64String) {
        try {
            org.apache.tomcat.util.codec.binary.Base64.decodeBase64(base64String);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * description: helper method to call and update inventory-service.
     *
     * @param productId: id of the product to update or create
     * @param quantity: quantity of the product
     */
    private void notifyInventoryService(String productId, int quantity) {
        String updateInventoryUrl =
                INVENTORY_SERVICE_URL + "/update/productId=" + productId + "?quantity=" + quantity;
        restTemplate.postForObject(updateInventoryUrl, null, Void.class);
    }
}
