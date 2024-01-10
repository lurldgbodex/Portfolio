package tech.sgcor.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
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
    private String urlPath;
    private Product product;
    private Product product1;
    private Product product2;
    private Product product3;
    private String image64;
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

    @BeforeEach
    public void setUp() {
        byte[] imageData = "image.png".getBytes();

        product = Product
                .builder()
                .name("Iphone")
                .categoryId("Gadgets category")
                .description("Apple phone")
                .quantity(10)
                .imageData(imageData)
                .price(BigDecimal.valueOf(1000))
                .build();

        image64 = Base64.getEncoder().encodeToString(product.getImageData());

        var createdProduct = productRepository.save(product);
        urlPath = "/api/products/" + createdProduct.getId();

        product1 = Product
                .builder()
                .name("new product")
                .price(BigDecimal.valueOf(10))
                .description("A new product")
                .quantity(50)
                .imageData(imageData)
                .categoryId("gadgets")
                .build();

        product2 = Product
                .builder()
                .name("another product")
                .price(BigDecimal.valueOf(100))
                .description("Another new product")
                .quantity(4)
                .imageData(imageData)
                .categoryId("products")
                .build();

        product3 = Product
                .builder()
                .name("Macbook")
                .price(BigDecimal.valueOf(5000))
                .description("Apple pc")
                .quantity(4)
                .imageData(imageData)
                .categoryId("gadgets")
                .build();

        Product product4 = Product
                .builder()
                .name("iphone 12")
                .categoryId("Gadgets category")
                .description("Apple phone")
                .quantity(10)
                .imageData(imageData)
                .price(BigDecimal.valueOf(1000))
                .build();

        List<Product> products =new ArrayList<>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);

        productRepository.saveAll(products);
    }
    @Test
    @DirtiesContext
    void CreateProductTest() throws Exception {
        // Test for successful product creation
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


        // Test create a product with invalid request
        ProductRequest InvalidRequest = new ProductRequest();

        String invalidRequestString = objectMapper.writeValueAsString(InvalidRequest);

        mockMvc.perform(post("/api/products/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestString))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors").isMap())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"));
    }

    @Test
    @DirtiesContext
    void GetProductTest() throws Exception {
        // Test to get product by id
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

        // Test to get product with invalid id
        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.error").value("product not found with id"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));

        // Test to get product by categoryId
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

        // Test to get all products
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());

        // Test search product by name
        var searchResponse = mockMvc.perform(get("/api/products/search?keyword=product")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        List<Product> searchResponseList = parseResponse(searchResponse.getResponse().getContentAsString());
        assertThat(searchResponseList).hasSize(2);
        assertThat(searchResponseList).anyMatch((name)-> name.getName().equals("new product"));
        assertThat(searchResponseList).anyMatch((name)-> name.getName().equals("another product"));

        // another test search by name with sort
        var result = mockMvc.perform(get("/api/products/search?keyword=iphone")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        List<Product> response = parseResponse(result.getResponse().getContentAsString());
        assertThat(response).hasSize(2);
        assertThat(response).first().matches((name)-> name.getName().equals("Iphone"));
        assertThat(response).anyMatch((name)-> name.getName().equals("iphone 12"));

        // test search without keyword
        mockMvc.perform(get("/api/products/search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private List<Product> parseResponse(String contentAsString) {
        try {
            return objectMapper.readValue(contentAsString, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Test
    void UpdateProductTest() throws Exception {
        // Test successful update
        UpdateProduct request = UpdateProduct
                .builder()
                .name("update name")
                .quantity(20)
                .build();

        String updateRequestString = objectMapper.writeValueAsString(request);

        // call the update endpoint with request
        mockMvc.perform(put(urlPath)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequestString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("product updated successfully"));

        // call get endpoint to verify successful update
        mockMvc.perform(get(urlPath)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("update name"))
                .andExpect(jsonPath("$.quantity").value(20));

        // Test update for non-existing product
        UpdateProduct badRequest = new UpdateProduct();
        String requestString = objectMapper.writeValueAsString(badRequest);

        mockMvc.perform(put("/api/products/38943")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("product not found"));

        // Test update with no request data
        mockMvc.perform(put(urlPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.error").value("you need to provide the field you want to update"));
    }

    @Test
    void DeleteProductTest() throws Exception {
        // test product delete
        mockMvc.perform(delete(urlPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("product deleted successfully"));

        // test delete for non-existing product
        mockMvc.perform(get(urlPath)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void CreateCategoryTest() throws Exception {
        // successful category creation test
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

        // create category with invalid request
        CategoryDto invalidRequest = new CategoryDto();

        String requestString = objectMapper.writeValueAsString(invalidRequest);

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
    void getCategoryTest() throws Exception {
        // Test successful get category
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

        // test Get category for non-existing category
        mockMvc.perform(get("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.error").value("category not found with id"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));

        // test to get all categories
        Category category2 = Category
                .builder()
                .name("another category")
                .description("Another new category")
                .build();

        categoryRepository.save(category2);

        mockMvc.perform(get("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        // update category success test
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

        // update category with non-existing category
        CategoryUpdate invalidRequest = new CategoryUpdate();
        String requestString = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(put("/api/categories/38943")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("category not found with id"));

        // update category with no data

        mockMvc.perform(put(productUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestString))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.error").value("you need to provide the field to update"));
    }

    @Test
    void DeleteCategoryTest() throws Exception {
        // delete category success test
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

        // delete non-existing category
        mockMvc.perform(get(productUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
