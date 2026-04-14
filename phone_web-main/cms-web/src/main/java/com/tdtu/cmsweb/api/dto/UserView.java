package com.tdtu.cmsweb.api.dto;

public record UserView(
        Long id,
        String userName,
        String fullName,
        String email,
        String address,
        String phoneNumber,
        String avatar,
        Boolean firstLogin,
        Boolean isActivated,
        Boolean isDeleted,
        String roleName
) {
}
