package com.tdtu.coreapi.auth;

import com.tdtu.coreapi.auth.dto.LoginRequest;
import com.tdtu.coreapi.auth.dto.LoginResult;
import com.tdtu.coreapi.auth.dto.LoginUserView;
import com.tdtu.coreapi.auth.dto.ForgotPasswordResult;
import com.tdtu.coreapi.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody java.util.Map<String, Object> payload) {
        try {
            Long userId = ((Number) payload.get("userId")).longValue();
            String newPassword = (String) payload.get("newPassword");
            authService.changeFirstPassword(userId, newPassword);
            return ApiResponse.success(null);
        } catch (Exception ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ApiResponse<ForgotPasswordResult> forgotPassword(@RequestBody java.util.Map<String, String> payload) {
        try {
            return ApiResponse.success(authService.forgotPassword(payload.get("email")));
        } catch (Exception ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody java.util.Map<String, String> payload) {
        try {
            authService.resetPassword(payload.get("token"), payload.get("newPassword"));
            return ApiResponse.success(null);
        } catch (Exception ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
    @PostMapping("/login")
    public ApiResponse<LoginResult> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginUserView user = authService.login(request);
            if (user == null) {
                return ApiResponse.error("User not found");
            }
            return ApiResponse.success(LoginResult.from(user));
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}
