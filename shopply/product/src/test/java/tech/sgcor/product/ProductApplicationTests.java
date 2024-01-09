package tech.sgcor.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import tech.sgcor.product.dto.CategoryDto;
import tech.sgcor.product.dto.CategoryUpdate;
import tech.sgcor.product.dto.ProductRequest;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.model.Category;
import tech.sgcor.product.model.Product;
import tech.sgcor.product.repository.CategoryRepository;
import tech.sgcor.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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
    @Autowired
    private CategoryRepository categoryRepository;

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
       byte[] image = "image.png".getBytes();
       String encodeImage = Base64.getEncoder().encodeToString(image);

        ProductRequest request = ProductRequest
                .builder()
                .name("iphone 14")
                .description("Apple phone")
                .category_id("phone category")
                .price(BigDecimal.valueOf(1300))
                .quantity(10)
                .image_data(encodeImage)
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
        byte[] imageData = "image.png".getBytes();

        Product product = Product
                .builder()
                .name("Iphone")
                .categoryId("Gadgets category")
                .description("Apple phone")
                .quantity(10)
                .imageData(imageData)
                .price(BigDecimal.valueOf(1000))
                .build();

        String image64 = Base64.getEncoder().encodeToString(product.getImageData());

        var createdProduct = productRepository.save(product);
        var urlPath = "/api/products/" + createdProduct.getId();

        mockMvc.perform(get(urlPath)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(product.getName()))
                .andExpect(jsonPath("$.category_id").value(product.getCategoryId()))
                .andExpect(jsonPath("$.description").value(product.getDescription()))
                .andExpect(jsonPath("$.price").value(product.getPrice()))
                .andExpect(jsonPath("$.quantity").value(product.getQuantity()))
                .andExpect(jsonPath("image_data").value(image64));

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
    void shouldGetProductsByCategory() throws Exception {
        byte[] imageData = "image.png".getBytes();

        Product product1 = Product
                .builder()
                .name("new product")
                .price(BigDecimal.valueOf(10))
                .description("A new product")
                .quantity(50)
                .imageData(imageData)
                .categoryId("gadgets")
                .build();

        Product product2 = Product
                .builder()
                .name("another product")
                .price(BigDecimal.valueOf(100))
                .description("Another new product")
                .quantity(4)
                .imageData(imageData)
                .categoryId("products")
                .build();

        Product product3 = Product
                .builder()
                .name("Macbook Pro")
                .price(BigDecimal.valueOf(5000))
                .description("Apple pc")
                .quantity(4)
                .imageData(imageData)
                .categoryId("gadgets")
                .build();

        List<Product> products =new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);

        productRepository.saveAll(products);

        var res = mockMvc.perform(get("/api/products/categories/gadgets")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andReturn();

        List<Product> resultList = parseResponse(res.getResponse().getContentAsString());

        assertThat(resultList).hasSize(2);
       assertThat(resultList).anyMatch(id-> id.getId().equals(product1.getId()));
       assertThat(resultList).anyMatch(id-> id.getId().equals(product3.getId()));
       assertThat(resultList).noneMatch(id-> id.getId().equals(product2.getId()));
    }

    private List<Product> parseResponse(String contentAsString) {
        try {
            return objectMapper.readValue(contentAsString, new TypeReference<List<Product>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        byte[] imageData = "image.png".getBytes();

        Product product1 = Product
                .builder()
                .name("new product")
                .price(BigDecimal.valueOf(10))
                .description("A new product")
                .quantity(50)
                .imageData(imageData)
                .categoryId("unknown")
                .build();

        Product product2 = Product
                .builder()
                .name("another product")
                .price(BigDecimal.valueOf(100))
                .description("Another new product")
                .quantity(4)
                .imageData(imageData)
                .categoryId("products")
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
        byte[] src = "image.png".getBytes();
        String image64 = Base64.getEncoder().encodeToString(src);

        ProductRequest createProduct = ProductRequest
                .builder()
                .name("create Product")
                .category_id("products")
                .description("a product")
                .quantity(1)
                .image_data(image64)
                .price(BigDecimal.valueOf(20))
                .build();
        String createProductString = objectMapper.writeValueAsString(createProduct);

        UpdateProduct request = UpdateProduct
                .builder()
                .name("update name")
                .quantity(20)
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
                .andExpect(jsonPath("$.name").value("update name"))
                .andExpect(jsonPath("$.quantity").value(20));
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
        byte[] imageSrc = "image.png".getBytes();
        String image64 = Base64.getEncoder().encodeToString(imageSrc);

        ProductRequest createProduct = ProductRequest
                .builder()
                .name("create Product")
                .category_id("products")
                .description("a product")
                .price(BigDecimal.valueOf(20))
                .quantity(8)
                .image_data(image64)
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
        byte[] imageSrc = "image.png".getBytes();
        String image64 = Base64.getEncoder().encodeToString(imageSrc);

        ProductRequest createProduct = ProductRequest
                .builder()
                .name("create Product")
                .category_id("products")
                .description("a product")
                .quantity(2)
                .image_data(image64)
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


    @Test
    void shouldCreateCategory() throws Exception {
        CategoryDto request = CategoryDto
                .builder()
                .name("phone")
                .description("Apple phone")
                .build();

        String requestToString = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/categories/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestToString))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Category created successfully"));
    }

    @Test
    void shouldNotCreateCategoryWithInvalidRequest() throws Exception {
        CategoryDto request = new CategoryDto();

        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/categories/create")
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
    void shouldGetCategoryWithId() throws Exception {

        Category category = Category
                .builder()
                .name("Iphone")
                .description("Apple phone")
                .build();


        var createdCategory = categoryRepository.save(category);
        var urlPath = "/api/categories/" + createdCategory.getId();

        mockMvc.perform(get(urlPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(jsonPath("$.description").value(category.getDescription()));
    }

    @Test
    void getCategoryShouldReturn404IfNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.error").value("category not found with id"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));
    }

    @Test
    void shouldGetAllCategories() throws Exception {
        Category category = Category
                .builder()
                .name("new category")
                .description("A new category")
                .build();

       Category category2 = Category
                .builder()
                .name("another category")
                .description("Another new category")
                .build();

        List<Category> products =new ArrayList<>();
        products.add(category);
        products.add(category2);

        categoryRepository.saveAll(products);

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldUpdateCategory() throws Exception {
      CategoryDto createCategory = CategoryDto
                .builder()
                .name("category")
                .description("a product category")
                .build();
        String createProductString = objectMapper.writeValueAsString(createCategory);

        CategoryUpdate request = new CategoryUpdate();
        request.setName("update name");

        String updateRequestString = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post("/api/categories/create")
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
                .andExpect(jsonPath("$.message").value("category updated successfully"));

        mockMvc.perform(get(productUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()));
    }

    @Test
    void shouldFailCategoryUpdateIfNotFound() throws Exception {
       CategoryUpdate request = new CategoryUpdate();
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/categories/38943")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("category not found with id"));
    }

    @Test
    void shouldFailCategoryUpdateWithNoData() throws Exception {
     CategoryDto createCategory = CategoryDto
                .builder()
                .name("category")
                .description("description")
                .build();
        String createProductString = objectMapper.writeValueAsString(createCategory);

       CategoryUpdate request = new CategoryUpdate();

        String updateRequestString = objectMapper.writeValueAsString(request);

        var response = mockMvc.perform(post("/api/categories/create")
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
                .andExpect(jsonPath("$.error").value("you need to provide the field to update"));
    }

    @Test
    void shouldDeleteCategory() throws Exception {
       CategoryDto createCategory = CategoryDto
                .builder()
                .name("create category")
                .description("a category")
                .build();
        String createProductString = objectMapper.writeValueAsString(createCategory);

        var response = mockMvc.perform(post("/api/categories/create")
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
                .andExpect(jsonPath("$.message").value("category deleted successfully"));

        mockMvc.perform(get(productUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
