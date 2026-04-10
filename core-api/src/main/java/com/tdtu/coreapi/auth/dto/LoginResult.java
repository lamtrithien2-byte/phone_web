package com.tdtu.coreapi.auth.dto;

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
    public static LoginResult from(LoginUserView user) {
        return new LoginResult(
                user.id(),
                user.userName(),
                user.fullName(),
                user.email(),
                user.address(),
                user.phoneNumber(),
                user.avatar(),
                user.isActivated(),
                user.firstLogin(),
                user.isDeleted(),
                user.roleName()
        );
    }
}
