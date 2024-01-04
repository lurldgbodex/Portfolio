package tech.sgcor.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.sgcor.product.dto.ProductRequest;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.model.Product;
import tech.sgcor.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public class ProductApplicationTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

    @Container
    static MongoDBContainer mongoDBContainer =
            new MongoDBContainer("mongo:4.4.2");
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri",
                mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = ProductRequest
                .builder()
                .name("iphone 14")
                .description("Apple phone")
                .category("Phone")
                .price(BigDecimal.valueOf(1300))
                .build();

        String requestToString = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/products/create")
                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestToString))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("product created successfully"));

        assertThat(productRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    void shouldNotCreateProductWithInvalidRequest() throws Exception {
        ProductRequest request = new ProductRequest();

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/products/create")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    void shouldGetProductWithId() throws Exception {
        Product product = Product
                .builder()
                .name("Iphone")
                .category("Gadgets")
                .description("Apple phone")
                .price(BigDecimal.valueOf(1000))
                .build();

        var createdProduct = productRepository.save(product);
        var urlPath = "/api/products/" + createdProduct.getId();

        mockMvc.perform(get(urlPath)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.category").value(product.getCategory()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()));

    }

    @Test
    void getProductShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.error").value("product not found with id"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        Product product1 = Product
                .builder()
                .name("new product")
                .price(BigDecimal.valueOf(10))
                .description("A new product")
                .category("unknown")
                .build();

        Product product2 = Product
                .builder()
                .name("another product")
                .price(BigDecimal.valueOf(100))
                .description("Another new product")
                .category("products")
                .build();

        List<Product> products =new ArrayList<>();
        products.add(product1);
        products.add(product2);

        productRepository.saveAll(products);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Product createProduct = Product
                .builder()
                .name("create Product")
                .category("products")
                .description("a product")
                .price(BigDecimal.valueOf(20))
                .build();
        String createProductString = objectMapper.writeValueAsString(createProduct);

        UpdateProduct request = UpdateProduct
                .builder()
                .name("update name")
                .build();

        String updateRequestString = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createProductString))
                .andExpect(status().isCreated())
                .andReturn();

        String productUrl = response.getResponse().getHeader("Location");

        assert productUrl != null;
        mockMvc.perform(put(productUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("product updated successfully"));

        mockMvc.perform(get(productUrl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("update name"));
    }

    @Test
    void shouldFailProductUpdateIfNotFound() throws Exception {
        UpdateProduct request = new UpdateProduct();
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/products/38943")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("product not found"));
    }

    @Test
    void shouldFailProductUpdateWithNoData() throws Exception {
        Product createProduct = Product
                .builder()
                .name("create Product")
                .category("products")
                .description("a product")
                .price(BigDecimal.valueOf(20))
                .build();
        String createProductString = objectMapper.writeValueAsString(createProduct);

        UpdateProduct request = new UpdateProduct();

        String updateRequestString = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductString))
                .andExpect(status().isCreated())
                .andReturn();

        String productUrl = response.getResponse().getHeader("Location");

        assert productUrl != null;
        mockMvc.perform(put(productUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.error").value("you need to provide the field you want to update"));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Product createProduct = Product
                .builder()
                .name("create Product")
                .category("products")
                .description("a product")
                .price(BigDecimal.valueOf(20))
                .build();
        String createProductString = objectMapper.writeValueAsString(createProduct);

        var response = mockMvc.perform(post("/api/products/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createProductString))
                .andExpect(status().isCreated())
                .andReturn();

        String productUrl = response.getResponse().getHeader("Location");

        assert productUrl != null;

        mockMvc.perform(delete(productUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("product deleted successfully"));

        mockMvc.perform(get(productUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
