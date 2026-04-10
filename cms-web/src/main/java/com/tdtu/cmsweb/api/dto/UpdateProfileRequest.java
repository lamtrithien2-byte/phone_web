package com.tdtu.cmsweb.api.dto;

public record UpdateProfileRequest(
        Long userId,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        String avatar
) {
}
