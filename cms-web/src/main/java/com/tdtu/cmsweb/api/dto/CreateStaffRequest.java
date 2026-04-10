package com.tdtu.cmsweb.api.dto;

public record CreateStaffRequest(
        String userName,
        String password,
        String fullName,
        String email,
        String address,
        String phoneNumber
) {
}
