package tech.sgcor.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tech.sgcor.cart.dto.CartTotalResponse;
import tech.sgcor.cart.dto.CustomResponse;
import tech.sgcor.cart.model.CartItem;
import tech.sgcor.cart.service.CartService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("/{userId}/add")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse addToCart(@PathVariable(name = "userId") String userId,
                                    @RequestBody @Valid CartItem cartItem) {
        return cartService.addToCart(userId, cartItem);
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse removeFromCart(@PathVariable(name = "userId") String userId,
                                         @PathVariable(name = "productId") String productId) {
        return cartService.removeFromCart(userId, productId);
    }

    @PutMapping("/{userId}/update-quantity/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse updateCartItemQuantity(@PathVariable(name = "userId") String userId,
                                       @PathVariable(name = "productId") String productId, @RequestBody int quantity) {
        return cartService.updateCartItemQuantity(userId, productId, quantity);
    }

    @DeleteMapping("/{userId}/clear")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse clearCart(@PathVariable(name = "userId") String userId) {
        return cartService.clearCart(userId);
    }

    @GetMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.OK)
    public List<CartItem> getCartItem(@PathVariable(name = "userId") String userId) {
        return cartService.getCartItem(userId);
    }

    @GetMapping("/{userId}/total")
    @ResponseStatus(HttpStatus.OK)
    public CartTotalResponse calculateCartTotal(@PathVariable(name = "userId") String userId) {
        return cartService.calculateCartTotal(userId);
    }
}
