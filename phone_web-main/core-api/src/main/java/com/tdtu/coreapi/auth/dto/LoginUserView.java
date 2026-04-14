package com.tdtu.coreapi.auth.dto;

public record LoginUserView(
        Long id,
        String userName,
        String password,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        String avatar,
        String activationToken,
        java.util.Date activationExpires,
        Boolean isActivated,
        Boolean firstLogin,
        Boolean isDeleted,
        String roleName
) {
}
