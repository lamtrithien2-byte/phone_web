package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.ProductView;
import com.tdtu.cmsweb.api.dto.ProductUpsertRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductApiClient {

    private static final ParameterizedTypeReference<ApiResponse<List<ProductView>>> PRODUCT_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<Long>> PRODUCT_CREATE_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<Void>> VOID_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public ProductApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<ProductView> getProducts(String keyword) {
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

    public Long create(ProductUpsertRequest product) {
        ApiResponse<Long> response = restClient.post()
                .uri("/api/products")
                .body(product)
                .retrieve()
                .body(PRODUCT_CREATE_RESPONSE);
        return response != null ? response.data() : null;
    }

    public void update(ProductUpsertRequest product) {
        restClient.put()
                .uri("/api/products")
                .body(product)
                .retrieve()
                .body(VOID_RESPONSE);
    }

    public void delete(String barCode) {
        restClient.delete()
                .uri("/api/products/{barCode}", barCode)
                .retrieve()
                .body(VOID_RESPONSE);
    }
}
