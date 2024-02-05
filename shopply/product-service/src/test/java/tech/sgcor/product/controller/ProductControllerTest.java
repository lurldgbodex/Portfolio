package tech.sgcor.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.dto.ProductRequest;
import tech.sgcor.product.dto.ProductResponse;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.service.ProductService;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ProductController underTest;
    @Mock
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateMockMvc() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest request = ProductRequest
                .builder()
                .name("new product")
                .description("description")
                .price(BigDecimal.valueOf(108))
                .category_id("category")
                .quantity(1)
                .image_data("imageData")
                .build();

        String requestString = objectMapper.writeValueAsString(request);

        URI createdObject = UriComponentsBuilder.fromPath("/api/products/38894").build().toUri();
        var expectedRes = new CustomResponse(201, "Product created successfully");

        when(underTest.createProduct(request))
                .thenReturn(ResponseEntity.created(createdObject).body(expectedRes));

        mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Product created successfully"));
    }

    @Test
    void getProductTest() throws Exception {
        ProductResponse res = ProductResponse
                .builder()
                .id("productId")
                .name("product")
                .category_id("product category")
                .description("product description")
                .quantity(10)
                .image_data("image_data.png")
                .price(BigDecimal.valueOf(44))
                .build();
        when(underTest.getProduct("productId")).thenReturn(res);

        mockMvc.perform(get("/api/products/productId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(res.getName()))
                .andExpect(jsonPath("$.id").value(res.getId()))
                .andExpect(jsonPath("$.category_id").value(res.getCategory_id()))
                .andExpect(jsonPath("$.quantity").value(res.getQuantity()))
                .andExpect(jsonPath("$.image_data").value(res.getImage_data()))
                .andExpect(jsonPath("$.price").value(res.getPrice()))
                .andExpect(jsonPath("$.description").value(res.getDescription()));
    }

    @Test
    void getAllProductsTest() throws Exception {
        List<ProductResponse> products = new ArrayList<>();
        when(underTest.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void updateProductTest() throws Exception {
        UpdateProduct req = UpdateProduct
                .builder()
                .name("update")
                .build();
        CustomResponse res = new CustomResponse(200, "updated");
        when(underTest.updateProduct("34", req)).thenReturn(res);

        String reqString = objectMapper.writeValueAsString(req);

        mockMvc.perform(put("/api/products/34")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqString))
                .andExpect(jsonPath("$.code").value(res.code()))
                .andExpect(jsonPath("$.message").value(res.message()));
    }

    @Test
    void deleteProductTest() throws Exception {
        CustomResponse res = new CustomResponse(200, "delete");
        when(underTest.deleteProduct("38")).thenReturn(res);

        mockMvc.perform(delete("/api/products/38")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(res.code()))
                .andExpect(jsonPath("$.message").value(res.message()));
    }
}