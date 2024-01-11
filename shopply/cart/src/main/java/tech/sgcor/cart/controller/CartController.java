package tech.sgcor.cart.controller;

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
    private CartService cartService;

    @PostMapping("/{userId}/add")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse addToCart(@PathVariable String userId, @RequestBody CartItem cartItem) {
        return cartService.addToCart(userId, cartItem);
    }

    @PutMapping("/{userId}/remove/{ProductId}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse removeFromCart(@PathVariable String userId, @PathVariable String productId) {
        return cartService.removeFromCart(userId, productId);
    }

    @PutMapping("/{userId}/update-quantity/{productId}/{quantity}")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse updateCartItemQuantity(@PathVariable String userId,
                                       @PathVariable String productId, @PathVariable int quantity) {
        return cartService.updateCartItemQuantity(userId, productId, quantity);
    }

    @PutMapping("/{userId}/clear")
    @ResponseStatus(HttpStatus.OK)
    public CustomResponse clearCart(@PathVariable String userId) {
        return cartService.clearCart(userId);
    }

    @GetMapping("/{userId}/items")
    @ResponseStatus(HttpStatus.OK)
    public List<CartItem> getCartItem(@PathVariable String userId) {
        return cartService.getCartItem(userId);
    }

    @GetMapping("/{userId}/total")
    @ResponseStatus(HttpStatus.OK)
    public CartTotalResponse calculateCartTotal(@PathVariable String userId) {
        return cartService.calculateCartTotal(userId);
    }
}
