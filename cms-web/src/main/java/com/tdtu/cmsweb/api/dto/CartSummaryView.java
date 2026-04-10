package com.tdtu.cmsweb.api.dto;

import java.util.List;

public record CartSummaryView(
        Integer totalQuantity,
        Integer totalAmount,
        List<CartItemView> items
) {
}
