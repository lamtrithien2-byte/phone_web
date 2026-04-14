package com.tdtu.coreapi.cart.dto;

public record CartItemView(
        Long id,
        Long productId,
        Long salePeopleId,
        String imageLink,
        String name,
        Integer quantity,
        Integer salePrice,
        Integer totalMoney
) {
}
