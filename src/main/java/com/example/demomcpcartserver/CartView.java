package com.example.demomcpcartserver;

import java.util.List;

public record CartView(String userId, List<CartItem> items, int totalItems) {
}
