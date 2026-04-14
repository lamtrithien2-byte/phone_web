package com.tdtu.coreapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateProfileRequest(
        @NotNull Long userId,
        @NotBlank String fullName,
        @Email String email,
        @NotBlank String address,
        @NotBlank String phoneNumber,
        String avatar
) {
}
