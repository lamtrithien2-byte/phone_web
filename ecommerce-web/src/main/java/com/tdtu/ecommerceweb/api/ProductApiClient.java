package com.tdtu.ecommerceweb.api;

import com.tdtu.ecommerceweb.api.dto.ProductView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductApiClient {

    private static final ParameterizedTypeReference<ApiResponse<List<ProductView>>> PRODUCT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public ProductApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductView> findProducts(String keyword) {
        ApiResponse<List<ProductView>> response;
        if (keyword == null || keyword.isBlank()) {
            response = restClient.get()
                    .uri("/api/products")
                    .retrieve()
                    .body(PRODUCT_RESPONSE);
        } else {
            response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/products/search").queryParam("keyword", keyword).build())
                    .retrieve()
                    .body(PRODUCT_RESPONSE);
        }
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
