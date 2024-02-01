package tech.sgcor.cart;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import tech.sgcor.cart.dto.CartTotalResponse;
import tech.sgcor.cart.dto.CustomResponse;
import tech.sgcor.cart.exception.CustomError;
import tech.sgcor.cart.model.CartItem;
import tech.sgcor.cart.service.CartService;

import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CartApplicationTest {
    @Autowired
    private CartService cartService;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String, CartItem> redisTemplate;

    @LocalServerPort
    int randomServerPort;

    @Container
    private static final RedisContainer redisContainer =
            new RedisContainer(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379);


    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeEach
    public void setup() {
    }

    @Test
    void checkIfRedisContainerIsRunning() {
        assertThat(redisContainer.isRunning()).isTrue();
    }

    @Test
    void addToCart_ShouldAddItemToCart() throws URISyntaxException {
        String userId = "user123";
        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/add";

        CartItem cartItem = new CartItem("productId", "product 1", 10.0, 2);
        HttpEntity<CartItem> requestEntity = new HttpEntity<>(cartItem);

        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, CustomResponse.class, userId);

        CustomResponse response = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("items added to cart");

        // check item successfully added
        List<CartItem> items =  cartService.getCartItem(userId);
        assertThat(items).isNotEmpty();
        assertThat(items).contains(cartItem);
    }

    @Test
    void addToCart_ShouldThrowErrorIfInvalidCartItem() {
        String userId = "user1233";
        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CartItem> requestEntity = new HttpEntity<>(new CartItem(), headers);

        ResponseEntity<CustomError> responseEntity = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, CustomError.class, userId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        CustomError error = responseEntity.getBody();
        assertThat(error).isNotNull();
        assertThat(error.code()).isEqualTo(400);
        assertThat(error.status()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(error.errors()).isNotEmpty();
        assertThat(error.errors()).hasFieldOrPropertyWithValue("productId", "productId must be provided");
        assertThat(error.errors()).hasFieldOrPropertyWithValue("productName", "productName must be provided");
        assertThat(error.errors()).hasFieldOrPropertyWithValue("price", "price must be provided");
        assertThat(error.errors()).hasFieldOrPropertyWithValue("quantity", "quantity must be provided");
    }

    @Test
    void removeFromCart_shouldRemoveFromCart() {
        String userId = "user123";
        String productId = "productId";

        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/remove/{productId}";

        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.DELETE, null, CustomResponse.class, userId, productId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("item removed from cart");
    }

    @Test
    void removeFromCart_NoProductFound() {
        String userId = "user123";
        String productId = "NonExistingProductId";

        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/remove/{productId}";

        ResponseEntity<CustomError> responseEntity = restTemplate.exchange(
                url, HttpMethod.DELETE, null, CustomError.class, userId, productId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        CustomError response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(404);
        assertThat(response.error()).isEqualTo("Status 404: product not found with id");
        assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateCartItemQuantity_ShouldUpdateCartItemQuantity() {
        //create cart item and add to database
        String userId = "user23";
        String productId = "product2839";
        int quantity = 5;

        CartItem cartItem = new CartItem(productId, "product name", 20.0, 10);
        cartService.addToCart(userId, cartItem);

        // create request entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Integer> requestEntity = new HttpEntity<Integer>(quantity, headers);

        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/update-quantity/{productId}";

        // send put request to update url
        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, CustomResponse.class, userId, productId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("cart item updated");
    }

    @Test
    void updateCartItemQuantity_ShouldFailUpdate() {
        //update with invalid quantity
        String userId = "user23";
        String productId = "product2839";
        int quantity = -5;

        // create request entity
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Integer> requestEntity = new HttpEntity<Integer>(quantity, headers);

        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/update-quantity/{productId}";

        // send put request to update url
        ResponseEntity<CustomError> responseEntity = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, CustomError.class, userId, productId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        CustomError response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(400);
        assertThat(response.error()).isEqualTo("Status 400: quantity cannot be less than 0");

        /**
         * update with non-existing-productId test
         */
        productId = "non-existing-productId";
        quantity = 10;

        requestEntity = new HttpEntity<Integer>(quantity, headers);

        // send put request to update url
        ResponseEntity<CustomError> newResponseEntity = restTemplate.exchange(
                url, HttpMethod.PUT, requestEntity, CustomError.class, userId, productId);

        assertThat(newResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        CustomError notFoundResponse = newResponseEntity.getBody();
        assertThat(notFoundResponse).isNotNull();
        assertThat(notFoundResponse.code()).isEqualTo(404);
        assertThat(notFoundResponse.error()).isEqualTo("Status 404: product not found with id");
    }

    @Test
    void clearCart_shouldClearCart() {
        String userId = "user23";
        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/clear";

        ResponseEntity<CustomResponse> responseEntity = restTemplate.exchange(
                url, HttpMethod.DELETE, null, CustomResponse.class, userId);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CustomResponse response = responseEntity.getBody();
        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("cart items cleared");
    }

    @Test
    void getCartItem_ShouldGetCartItems() {
        String userId = "user12345";
        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/items";

        //create cart items
        CartItem item1 = new CartItem("product1", "productName", 20.0, 5);
        CartItem item2 = new CartItem("product2", "productName", 8.0, 3);

        cartService.addToCart(userId, item1);
        cartService.addToCart(userId, item2);

        ParameterizedTypeReference<List<CartItem>> responseType = new ParameterizedTypeReference<List<CartItem>>() {};

        ResponseEntity<List<CartItem>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, responseType, userId);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<CartItem> cartItems= responseEntity.getBody();
        assertThat(cartItems).isNotEmpty();
        assertThat(cartItems).hasSize(2);
        assertThat(cartItems).contains(item1);
        assertThat(cartItems).contains(item2);
    }

    @Test
    void calculateCartTotal_ShouldCalculateCartTotal() {
        String userId = "user12345";
        final String url = "http://localhost:" + randomServerPort + "/api/carts/{userId}/total";

        ResponseEntity<CartTotalResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, CartTotalResponse.class, userId);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        CartTotalResponse cartTotal= responseEntity.getBody();
        assertThat(cartTotal).isNotNull();
        assertThat(cartTotal.total()).isEqualTo(124);
    }
}
