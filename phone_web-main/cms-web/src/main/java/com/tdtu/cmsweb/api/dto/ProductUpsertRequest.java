package com.tdtu.cmsweb.api.dto;

public record ProductUpsertRequest(
        String barCode,
        String name,
        String screenSize,
        String ram,
        String rom,
        Integer importPrice,
        Integer priceSale,
        String description,
        String imageLink,
        Integer saleNumber,
        Long categoryId
) {
}
