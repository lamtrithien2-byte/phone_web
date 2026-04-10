package com.tdtu.coreapi.product.dto;

import java.util.Date;

public record ProductView(
        Long id,
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
        Date createdDate,
        Date updatedDate,
        Boolean isDeleted,
        String categoryName
) {
}
