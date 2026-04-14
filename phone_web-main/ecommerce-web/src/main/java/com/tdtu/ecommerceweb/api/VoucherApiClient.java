package com.tdtu.ecommerceweb.api;

import com.tdtu.ecommerceweb.api.dto.VoucherValidationView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class VoucherApiClient {

    private static final ParameterizedTypeReference<ApiResponse<VoucherValidationView>> VOUCHER_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public VoucherApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public ApiResponse<VoucherValidationView> validate(String code, int subtotalMoney, int shippingFee) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/vouchers/validate")
                        .queryParam("code", code)
                        .queryParam("subtotalMoney", subtotalMoney)
                        .queryParam("shippingFee", shippingFee)
                        .build())
                .retrieve()
                .body(VOUCHER_RESPONSE);
    }
}
