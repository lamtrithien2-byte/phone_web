package com.tdtu.coreapi.statistics;

import com.tdtu.coreapi.statistics.dto.DashboardSummaryView;
import com.tdtu.coreapi.statistics.dto.StaffRevenueView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StatisticsProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public DashboardSummaryView getDashboardSummary() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_statistical_summary_with_users");
        Object[] row = (Object[]) query.getSingleResult();
        return new DashboardSummaryView(
                ((Number) row[0]).longValue(),
                ((Number) row[1]).intValue(),
                ((Number) row[2]).intValue(),
                ((Number) row[3]).intValue()
        );
    }

    public List<StaffRevenueView> getStaffRevenue() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_statistical_staff_revenue");
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new StaffRevenueView(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            ((Number) row[2]).intValue(),
                            ((Number) row[3]).intValue(),
                            ((Number) row[4]).longValue()
                    );
                })
                .toList();
    }
}
