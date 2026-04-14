package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.ForgotPasswordResult;
import com.tdtu.cmsweb.api.dto.LoginRequest;
import com.tdtu.cmsweb.api.dto.LoginResult;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthApiClient {

    private static final ParameterizedTypeReference<ApiResponse<LoginResult>> LOGIN_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<ForgotPasswordResult>> FORGOT_PASSWORD_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<Void>> VOID_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public AuthApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ApiResponse<LoginResult> login(LoginRequest request) {
        return restClient.post()
                .uri("/api/auth/login")
                .body(request)
                .retrieve()
                .body(LOGIN_RESPONSE);
    }

    public ApiResponse<Void> changePassword(Long userId, String newPassword) {
        return restClient.post()
                .uri("/api/auth/change-password")
                .body(java.util.Map.of("userId", userId, "newPassword", newPassword))
                .retrieve()
                .body(VOID_RESPONSE);
    }

    public ApiResponse<ForgotPasswordResult> forgotPassword(String email) {
        return restClient.post()
                .uri("/api/auth/forgot-password")
                .body(java.util.Map.of("email", email))
                .retrieve()
                .body(FORGOT_PASSWORD_RESPONSE);
    }

    public ApiResponse<Void> resetPassword(String token, String newPassword) {
        return restClient.post()
                .uri("/api/auth/reset-password")
                .body(java.util.Map.of("token", token, "newPassword", newPassword))
                .retrieve()
                .body(VOID_RESPONSE);
    }
}
