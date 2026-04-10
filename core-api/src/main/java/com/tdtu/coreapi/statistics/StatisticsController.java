package com.tdtu.coreapi.statistics;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.statistics.dto.DashboardSummaryView;
import com.tdtu.coreapi.statistics.dto.StaffRevenueView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<DashboardSummaryView> dashboardSummary() {
        return ApiResponse.success(statisticsService.getDashboardSummary());
    }

    @GetMapping("/staff-revenue")
    public ApiResponse<List<StaffRevenueView>> staffRevenue() {
        return ApiResponse.success(statisticsService.getStaffRevenue());
    }
}
