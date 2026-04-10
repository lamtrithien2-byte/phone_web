package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.OnlineOrderSummaryView;
import com.tdtu.cmsweb.api.dto.OrderDetailView;
import com.tdtu.cmsweb.api.dto.UpdateOrderStatusRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class OrderApiClient {

    private static final ParameterizedTypeReference<ApiResponse<List<OnlineOrderSummaryView>>> ORDER_SUMMARY_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<OrderDetailView>>> ORDER_DETAIL_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<Void>> VOID_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public OrderApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<OnlineOrderSummaryView> getAllOrders() {
        ApiResponse<List<OnlineOrderSummaryView>> response = restClient.get()
                .uri("/api/orders")
                .retrieve()
                .body(ORDER_SUMMARY_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }

    public List<OrderDetailView> getDetails(String orderCode) {
        ApiResponse<List<OrderDetailView>> response = restClient.get()
                .uri("/api/orders/{orderCode}", orderCode)
                .retrieve()
                .body(ORDER_DETAIL_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }

    public ApiResponse<Void> updateStatus(UpdateOrderStatusRequest request) {
        return restClient.put()
                .uri("/api/orders/status")
                .body(request)
                .retrieve()
                .body(VOID_RESPONSE);
    }
}
