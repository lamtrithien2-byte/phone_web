package com.tdtu.cmsweb.api.dto;

public record CustomerView(
        Long id,
        String fullName,
        String phoneNumber,
        String address
) {
}
