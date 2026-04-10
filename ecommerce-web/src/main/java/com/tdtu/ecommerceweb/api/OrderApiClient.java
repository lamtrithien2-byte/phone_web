package com.tdtu.ecommerceweb.api;

import com.tdtu.ecommerceweb.api.dto.CreateOrderRequest;
import com.tdtu.ecommerceweb.api.dto.OrderCreatedView;
import com.tdtu.ecommerceweb.api.dto.OrderDetailView;
import com.tdtu.ecommerceweb.api.dto.OrderSummaryView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OrderApiClient {

    private static final ParameterizedTypeReference<ApiResponse<OrderCreatedView>> CREATE_ORDER_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<OrderSummaryView>>> ORDER_SUMMARY_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<OrderDetailView>>> ORDER_DETAIL_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public OrderApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ApiResponse<OrderCreatedView> createOrder(CreateOrderRequest request) {
        return restClient.post()
                .uri("/api/orders")
                .body(request)
                .retrieve()
                .body(CREATE_ORDER_RESPONSE);
    }

    public List<OrderSummaryView> getOrdersByPhone(String phoneNumber) {
        ApiResponse<List<OrderSummaryView>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/orders/by-phone").queryParam("phoneNumber", phoneNumber).build())
                .retrieve()
                .body(ORDER_SUMMARY_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }

    public List<OrderDetailView> getOrderDetail(String orderCode) {
        ApiResponse<List<OrderDetailView>> response = restClient.get()
                .uri("/api/orders/{orderCode}", orderCode)
                .retrieve()
                .body(ORDER_DETAIL_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
