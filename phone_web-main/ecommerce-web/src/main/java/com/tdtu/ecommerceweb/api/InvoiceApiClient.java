package com.tdtu.ecommerceweb.api;

import com.tdtu.ecommerceweb.api.dto.CustomerView;
import com.tdtu.ecommerceweb.api.dto.InvoiceDetailView;
import com.tdtu.ecommerceweb.api.dto.PurchaseHistoryView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class InvoiceApiClient {

    private static final ParameterizedTypeReference<ApiResponse<CustomerView>> CUSTOMER_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<PurchaseHistoryView>>> HISTORY_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<InvoiceDetailView>>> DETAIL_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public InvoiceApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public CustomerView getCustomerByPhone(String phoneNumber) {
        ApiResponse<CustomerView> response = restClient.get()
                .uri("/api/invoices/customer/{phoneNumber}", phoneNumber)
                .retrieve()
                .body(CUSTOMER_RESPONSE);
        return response != null ? response.data() : null;
    }

    public List<PurchaseHistoryView> getHistory(Long customerId) {
        ApiResponse<List<PurchaseHistoryView>> response = restClient.get()
                .uri("/api/invoices/customer/{customerId}/history", customerId)
                .retrieve()
                .body(HISTORY_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }

    public List<InvoiceDetailView> getInvoiceDetail(String invoiceCode) {
        ApiResponse<List<InvoiceDetailView>> response = restClient.get()
                .uri("/api/invoices/{invoiceCode}/details", invoiceCode)
                .retrieve()
                .body(DETAIL_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
