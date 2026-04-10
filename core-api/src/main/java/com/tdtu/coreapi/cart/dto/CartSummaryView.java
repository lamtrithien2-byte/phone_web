package com.tdtu.coreapi.cart.dto;

import java.util.List;

public record CartSummaryView(
        Integer totalQuantity,
        Integer totalAmount,
        List<CartItemView> items
) {
}
