package com.tdtu.cmsweb.api;

import com.tdtu.cmsweb.api.dto.DashboardSummaryView;
import com.tdtu.cmsweb.api.dto.StaffRevenueView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class StatisticsApiClient {

    private static final ParameterizedTypeReference<ApiResponse<DashboardSummaryView>> SUMMARY_RESPONSE =
            new ParameterizedTypeReference<>() {
            };
    private static final ParameterizedTypeReference<ApiResponse<List<StaffRevenueView>>> STAFF_REVENUE_RESPONSE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    public StatisticsApiClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public DashboardSummaryView getDashboardSummary() {
        ApiResponse<DashboardSummaryView> response = restClient.get()
                .uri("/api/statistics/dashboard")
                .retrieve()
                .body(SUMMARY_RESPONSE);
        return response != null ? response.data() : null;
    }

    public List<StaffRevenueView> getStaffRevenue() {
        ApiResponse<List<StaffRevenueView>> response = restClient.get()
                .uri("/api/statistics/staff-revenue")
                .retrieve()
                .body(STAFF_REVENUE_RESPONSE);
        return response != null && response.data() != null ? response.data() : List.of();
    }
}
