package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.CategoryView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CategoryApiClient {

    private static final ParameterizedTypeReference<ApiResponse<List<CategoryView>>> CATEGORY_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public CategoryApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CategoryView> getAll() {
        ApiResponse<List<CategoryView>> response = restClient.get()
                .uri("/api/categories")
                .retrieve()
                .body(CATEGORY_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
