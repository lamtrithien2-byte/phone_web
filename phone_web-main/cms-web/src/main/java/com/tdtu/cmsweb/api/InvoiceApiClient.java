package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.CreateInvoiceRequest;
import com.tdtu.cmsweb.api.dto.InvoiceCreatedView;
import com.tdtu.cmsweb.api.dto.InvoiceDetailView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class InvoiceApiClient {

    private static final ParameterizedTypeReference<ApiResponse<InvoiceCreatedView>> INVOICE_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<InvoiceDetailView>>> INVOICE_DETAIL_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public InvoiceApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ApiResponse<InvoiceCreatedView> create(CreateInvoiceRequest request) {
        return restClient.post()
                .uri("/api/invoices")
                .body(request)
                .retrieve()
                .body(INVOICE_RESPONSE);
    }

    public List<InvoiceDetailView> getDetails(String invoiceCode) {
        ApiResponse<List<InvoiceDetailView>> response = restClient.get()
                .uri("/api/invoices/{invoiceCode}/details", invoiceCode)
                .retrieve()
                .body(INVOICE_DETAIL_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
