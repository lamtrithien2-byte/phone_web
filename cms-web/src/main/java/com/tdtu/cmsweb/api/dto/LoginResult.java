package com.tdtu.cmsweb.api.dto;

public record LoginResult(
        Long id,
        String userName,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        String avatar,
        Boolean isActivated,
        Boolean firstLogin,
        Boolean isDeleted,
        String roleName
) {
}
