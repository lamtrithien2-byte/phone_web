package com.tdtu.cmsweb.api.dto;

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
        java.util.Date createdDate,
        java.util.Date updatedDate,
        Boolean isDeleted,
        String categoryName
) {
}
