package com.example.demomcpcartserver;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class CartTools {

    private final CartService cartService;
    private final CurrentUserService currentUserService;

    public CartTools(CartService cartService, CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    @Tool(description = "Add a product to the current user's cart")
    public CartView addToCart(String sku, String name, int quantity) {
        return cartService.addItem(currentUserService.userId(), sku, name, quantity);
    }

    @Tool(description = "Remove a product quantity from the current user's cart")
    public CartView removeFromCart(String sku, int quantity) {
        return cartService.removeItem(currentUserService.userId(), sku, quantity);
    }

    @Tool(description = "List the current user's cart")
    public CartView getCart() {
        return cartService.view(currentUserService.userId());
    }

    @Tool(description = "Clear the current user's cart")
    public CartView clearCart() {
        return cartService.clear(currentUserService.userId());
    }
}