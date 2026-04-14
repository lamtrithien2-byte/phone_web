package com.tdtu.coreapi.customer;

import com.tdtu.coreapi.customer.dto.CustomerView;
import com.tdtu.coreapi.customer.dto.PurchaseHistoryView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomerView findByPhoneNumber(String phoneNumber) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_customer_get_by_phone");
        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.setParameter("p_phone_number", phoneNumber);

        List<Object[]> rows = query.getResultList();
        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = rows.get(0);
        return new CustomerView(
                ((Number) row[0]).longValue(),
                (String) row[3],
                (String) row[2],
                (String) row[5]
        );
    }

    public List<PurchaseHistoryView> getPurchaseHistory(Long customerId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_customer_purchase_history");
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.IN);
        query.setParameter("p_customer_id", customerId);
        return query.getResultList().stream()
                .map(row -> {
                    Object[] data = (Object[]) row;
                    return new PurchaseHistoryView(
                            (String) data[0],
                            (String) data[1],
                            (String) data[2],
                            ((Number) data[3]).intValue(),
                            ((Number) data[4]).intValue(),
                            ((Number) data[5]).intValue(),
                            ((Number) data[6]).intValue(),
                            (java.util.Date) data[7]
                    );
                })
                .toList();
    }
}
