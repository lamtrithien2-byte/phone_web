package com.tdtu.ecommerceweb.api.dto;

public record CustomerView(
        Long id,
        String customerCode,
        String phoneNumber,
        String fullName,
        String address
) {
}
