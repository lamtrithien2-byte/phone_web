package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.CustomerView;
import com.tdtu.cmsweb.api.dto.PurchaseHistoryView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CustomerApiClient {

    private static final ParameterizedTypeReference<ApiResponse<CustomerView>> CUSTOMER_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<PurchaseHistoryView>>> HISTORY_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public CustomerApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ApiResponse<CustomerView> findByPhone(String phoneNumber) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/customers/by-phone").queryParam("phoneNumber", phoneNumber).build())
                .retrieve()
                .body(CUSTOMER_RESPONSE);
    }

    public List<PurchaseHistoryView> getPurchaseHistory(Long customerId) {
        ApiResponse<List<PurchaseHistoryView>> response = restClient.get()
                .uri("/api/customers/{customerId}/purchase-history", customerId)
                .retrieve()
                .body(HISTORY_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
