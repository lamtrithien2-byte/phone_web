package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.AddCartItemRequest;
import com.tdtu.cmsweb.api.dto.CartSummaryView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CartApiClient {

    private static final ParameterizedTypeReference<ApiResponse<CartSummaryView>> CART_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public CartApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public CartSummaryView getCart(Long staffId) {
        ApiResponse<CartSummaryView> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/carts").queryParam("staffId", staffId).build())
                .retrieve()
                .body(CART_RESPONSE);
        return response != null ? response.data() : null;
    }

    public CartSummaryView addItem(AddCartItemRequest request) {
        ApiResponse<CartSummaryView> response = restClient.post()
                .uri("/api/carts/items")
                .body(request)
                .retrieve()
                .body(CART_RESPONSE);
        return response != null ? response.data() : null;
    }
}
