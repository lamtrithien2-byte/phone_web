package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.CreateStaffRequest;
import com.tdtu.cmsweb.api.dto.UpdateProfileRequest;
import com.tdtu.cmsweb.api.dto.UserView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class UserApiClient {

    private static final ParameterizedTypeReference<ApiResponse<UserView>> USER_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<UserView>>> USER_LIST_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public UserApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public UserView getProfile(Long userId) {
        ApiResponse<UserView> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/users/profile").queryParam("userId", userId).build())
                .retrieve()
                .body(USER_RESPONSE);
        return response != null ? response.data() : null;
    }

    public List<UserView> getStaffList() {
        ApiResponse<List<UserView>> response = restClient.get()
                .uri("/api/users/staff")
                .retrieve()
                .body(USER_LIST_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }

    public UserView createStaff(CreateStaffRequest request) {
        ApiResponse<UserView> response = restClient.post()
                .uri("/api/users/staff")
                .body(request)
                .retrieve()
                .body(USER_RESPONSE);
        return response != null ? response.data() : null;
    }

    public UserView updateProfile(UpdateProfileRequest request) {
        ApiResponse<UserView> response = restClient.put()
                .uri("/api/users/profile")
                .body(request)
                .retrieve()
                .body(USER_RESPONSE);
        return response != null ? response.data() : null;
    }

    public List<UserView> toggleStatus(Long userId) {
        ApiResponse<List<UserView>> response = restClient.post()
                .uri("/api/users/{userId}/toggle-status", userId)
                .retrieve()
                .body(USER_LIST_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
