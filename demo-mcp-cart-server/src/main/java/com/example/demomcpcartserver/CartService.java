package com.example.demomcpcartserver;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {

    private final Map<String, Map<String, CartItem>> carts = new ConcurrentHashMap<>();

    public CartView addItem(String userId, String sku, String name, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        Map<String, CartItem> cart = carts.computeIfAbsent(userId, ignored -> new LinkedHashMap<>());
        CartItem existing = cart.get(sku);
        if (existing == null) {
            cart.put(sku, new CartItem(sku, name, quantity));
        } else {
            cart.put(sku, new CartItem(sku, name, existing.quantity() + quantity));
        }
        return view(userId);
    }

    public CartView removeItem(String userId, String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }

        Map<String, CartItem> cart = carts.computeIfAbsent(userId, ignored -> new LinkedHashMap<>());
        CartItem existing = cart.get(sku);
        if (existing == null) {
            return view(userId);
        }

        int updatedQty = existing.quantity() - quantity;
        if (updatedQty <= 0) {
            cart.remove(sku);
        } else {
            cart.put(sku, new CartItem(sku, existing.name(), updatedQty));
        }
        return view(userId);
    }

    public CartView clear(String userId) {
        carts.remove(userId);
        return view(userId);
    }

    public CartView view(String userId) {
        Map<String, CartItem> cart = carts.getOrDefault(userId, Map.of());
        List<CartItem> items = new ArrayList<>(cart.values());
        int totalItems = items.stream().mapToInt(CartItem::quantity).sum();
        return new CartView(userId, items, totalItems);
    }
}
