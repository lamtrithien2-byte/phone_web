package com.tdtu.coreapi.customer.dto;

public record CustomerView(
        Long id,
        String fullName,
        String phoneNumber,
        String address
) {
}
