package tech.sgcor.cart.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tech.sgcor.cart.dto.CartTotalResponse;
import tech.sgcor.cart.dto.CustomResponse;
import tech.sgcor.cart.model.CartItem;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private static final String CART_PREFIX = "cart:";
    private final RedisTemplate<String, CartItem> redisTemplate;

    public CustomResponse addToCart(String userId, CartItem cartItem) {
        String cartKey = getCartKey(userId);
        redisTemplate.opsForHash().put(cartKey, cartItem.getProductId(), cartItem);
        return new CustomResponse(200, "items added to cart");
    }

    public CustomResponse removeFromCart(String userId, String productId) {
        String cartKey = getCartKey(userId);
        redisTemplate.opsForHash().delete(cartKey, productId);

        return new CustomResponse(200, "item removed from cart");
    }

    public CustomResponse updateCartItemQuantity(String userId, String productId, int quantity) {
        String cartKey = getCartKey(userId);
        CartItem cartItem = (CartItem) redisTemplate.opsForHash().get(cartKey, productId);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            redisTemplate.opsForHash().put(cartKey, productId, cartItem);
        }

        return new CustomResponse(200, "cart item updated");
    }

    public CustomResponse clearCart(String userId) {
        String cartKey = getCartKey(userId);
        redisTemplate.delete(cartKey);

        return new CustomResponse(200, "cart items cleared");
    }

    public List<CartItem> getCartItem(String userId) {
        String cartKey = getCartKey(userId);
        Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(cartKey);

        return cartEntries.values().stream()
                .map(entry -> (CartItem) entry)
                .toList();
    }

    public CartTotalResponse calculateCartTotal(String userId) {
        String cartKey = getCartKey(userId);
        Map<Object, Object> cartEntries = redisTemplate.opsForHash().entries(cartKey);

        return new CartTotalResponse(cartEntries.values().stream()
                .mapToDouble(entry -> ((CartItem) entry).getPrice() *
                        ((CartItem) entry).getQuantity())
                .sum());
    }

    private String getCartKey(String userId) {
        return CART_PREFIX + userId;
    }
}
