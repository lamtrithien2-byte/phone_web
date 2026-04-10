package com.tdtu.coreapi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateStaffRequest(
        @NotBlank String userName,
        @NotBlank String password,
        @NotBlank String fullName,
        @Email String email,
        @NotBlank String address,
        @NotBlank String phoneNumber
) {
}
