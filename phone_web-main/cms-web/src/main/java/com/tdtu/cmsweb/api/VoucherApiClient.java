package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.VoucherUpsertRequest;
import com.tdtu.cmsweb.api.dto.VoucherValidationView;
import com.tdtu.cmsweb.api.dto.VoucherView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class VoucherApiClient {

    private static final ParameterizedTypeReference<ApiResponse<List<VoucherView>>> LIST_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<VoucherView>> DETAIL_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<Long>> LONG_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<String>> STRING_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<Void>> VOID_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<VoucherValidationView>> VALIDATION_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public VoucherApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<VoucherView> getAll(String keyword, String status, String voucherType) {
        ApiResponse<List<VoucherView>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/vouchers")
                        .queryParam("keyword", keyword == null ? "" : keyword)
                        .queryParam("status", status == null ? "" : status)
                        .queryParam("voucherType", voucherType == null ? "" : voucherType)
                        .build())
                .retrieve()
                .body(LIST_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }

    public VoucherView getById(Long voucherId) {
        ApiResponse<VoucherView> response = restClient.get()
                .uri("/api/vouchers/{voucherId}", voucherId)
                .retrieve()
                .body(DETAIL_RESPONSE);
        return response != null ? response.data() : null;
    }

    public String generateCode() {
        ApiResponse<String> response = restClient.get()
                .uri("/api/vouchers/generate-code")
                .retrieve()
                .body(STRING_RESPONSE);
        return response != null ? response.data() : null;
    }

    public ApiResponse<VoucherValidationView> validate(String code, int subtotalMoney, int shippingFee) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/vouchers/validate")
                        .queryParam("code", code)
                        .queryParam("subtotalMoney", subtotalMoney)
                        .queryParam("shippingFee", shippingFee)
                        .build())
                .retrieve()
                .body(VALIDATION_RESPONSE);
    }

    public ApiResponse<Long> create(VoucherUpsertRequest request) {
        return restClient.post()
                .uri("/api/vouchers")
                .body(request)
                .retrieve()
                .body(LONG_RESPONSE);
    }

    public ApiResponse<Void> update(Long voucherId, VoucherUpsertRequest request) {
        return restClient.put()
                .uri("/api/vouchers/{voucherId}", voucherId)
                .body(request)
                .retrieve()
                .body(VOID_RESPONSE);
    }

    public ApiResponse<Void> toggle(Long voucherId, boolean active) {
        return restClient.put()
                .uri("/api/vouchers/{voucherId}/toggle", voucherId)
                .body(new java.util.LinkedHashMap<>(java.util.Map.of("active", active)))
                .retrieve()
                .body(VOID_RESPONSE);
    }

    public ApiResponse<Void> delete(Long voucherId) {
        return restClient.delete()
                .uri("/api/vouchers/{voucherId}", voucherId)
                .retrieve()
                .body(VOID_RESPONSE);
    }
}
