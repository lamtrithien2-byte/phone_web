package com.tdtu.coreapi.statistics;

import com.tdtu.coreapi.statistics.dto.DashboardSummaryView;
import com.tdtu.coreapi.statistics.dto.StaffRevenueView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final StatisticsProcedureRepository statisticsProcedureRepository;

    public StatisticsService(StatisticsProcedureRepository statisticsProcedureRepository) {
        this.statisticsProcedureRepository = statisticsProcedureRepository;
    }

    public DashboardSummaryView getDashboardSummary() {
        return statisticsProcedureRepository.getDashboardSummary();
    }

    public List<StaffRevenueView> getStaffRevenue() {
        return statisticsProcedureRepository.getStaffRevenue();
    }
}
