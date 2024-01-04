package tech.sgcor.product.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import tech.sgcor.product.dto.CustomResponse;
import tech.sgcor.product.dto.ProductRequest;
import tech.sgcor.product.dto.UpdateProduct;
import tech.sgcor.product.exception.BadRequestException;
import tech.sgcor.product.exception.ProductNotFoundException;
import tech.sgcor.product.model.Product;
import tech.sgcor.product.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService underTest;
    @Mock
    private ProductRepository productRepository;

    @Test
    void createProductSuccess() {
        ProductRequest request = ProductRequest
                .builder()
                .name("iphone 13")
                .category("Phone")
                .description("Apple product")
                .price(BigDecimal.valueOf(1000))
                .build();

        var res = underTest.createProduct(request);

        ArgumentCaptor<Product> productArgumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productArgumentCaptor.capture());
    }

    @Test
    void getProductSuccess() throws ProductNotFoundException {
        Product product = Product
                .builder()
                .id("89837")
                .name("MacBook pro")
                .category("pc")
                .price(BigDecimal.valueOf(5000))
                .description("Apple pc")
                .build();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        var getProduct = underTest.getProduct(product.getId());

        assertThat(getProduct.getId()).isEqualTo(product.getId());
        assertThat(getProduct.getName()).isEqualTo(product.getName());
        assertThat(getProduct.getDescription()).isEqualTo(product.getDescription());
        assertThat(getProduct.getPrice()).isEqualTo(product.getPrice());
        assertThat(getProduct.getCategory()).isEqualTo(product.getCategory());

        verify(productRepository, times(1)).findById(product.getId());
    }

    @Test
    void getProductFailure() {
        assertThatThrownBy(() -> underTest.getProduct("8398radix3"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("product not found with id");
    }

    @Test
    void getAllProductsSuccess() {
        Product product = Product
                .builder()
                .id("newProductID")
                .name("new product 1")
                .build();
        Product anotherProduct = Product
                .builder()
                .id("product2ID")
                .build();

        when(productRepository.findAll()).thenReturn(List.of(product, anotherProduct));

        var res = underTest.getAllProducts();

        List<Product> products = res.stream().map(productResponse -> Product
                .builder()
                .id(productResponse.getId())
                .name(productResponse.getName())
                .build()).toList();

        assertThat(products).isNotEmpty();
        assertThat(products).hasSize(2);
        assertThat(products).contains(product, anotherProduct);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProductsEmpty() {
        var products = underTest.getAllProducts();

        assertThat(products).isEmpty();
    }

    @Test
    void updateProductTest() {
        // Mock product
        Product product = Product
                .builder()
                .name("product name")
                .build();

        // Mock find product by id
        when(productRepository.findById("productId")).thenReturn(Optional.of(product));

        // create request to update
        UpdateProduct request = UpdateProduct
                .builder()
                .name("updated")
                .build();

        // Mock product save
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // call update method
        CustomResponse res = underTest.updateProduct("productId", request);

        // verify and assert response
        verify(productRepository).save(any(Product.class));
        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("product updated successfully");
    }

    @Test
    void updateProductNotFoundTest() {
        // Mock product
        Product product = Product
                .builder()
                .name("product name")
                .build();

        // Mock find product by id
        when(productRepository.findById("productId")).thenReturn(Optional.empty());

        // create request to update
        UpdateProduct request = UpdateProduct
                .builder()
                .name("updated")
                .build();

        // assertion
        assertThatThrownBy(() -> underTest.updateProduct("productId", request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("product not found");
    }

    @Test
    void updateProductBlankFieldTest() {
        // Mock product
        Product product = Product
                .builder()
                .name("product name")
                .build();

        // Mock find product by id
        when(productRepository.findById("productId")).thenReturn(Optional.of(product));

        // create request to update
        UpdateProduct request = new UpdateProduct();

        assertThatThrownBy(()-> underTest.updateProduct("productId", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("you need to provide the field you want to update");
    }

    @Test
    void deleteProductTest() {
        // Mock product
        Product product = Product
                .builder()
                .name("product name")
                .build();

        // mock find by id
        when(productRepository.findById("productId")).thenReturn(Optional.of(product));

        // call delete method
        CustomResponse res = underTest.deleteProduct("productId");

        // verify and asserts
        verify(productRepository, times(1)).findById("productId");
        verify(productRepository).delete(product);
        assertThat(res.code()).isEqualTo(200);
        assertThat(res.message()).isEqualTo("product deleted successfully");
    }

    @Test
    void deleteProductNotFoundTest() {
        // mock find by id
        when(productRepository.findById("productId")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.deleteProduct("productId"))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("product with id not found");
    }
}