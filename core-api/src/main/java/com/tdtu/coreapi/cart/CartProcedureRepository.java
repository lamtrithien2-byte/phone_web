package com.tdtu.coreapi.cart;

import com.tdtu.coreapi.cart.dto.CartItemView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CartProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<CartItemView> getCartByStaffId(Long staffId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_cart_get_by_staff");
        query.registerStoredProcedureParameter("p_staff_id", Long.class, ParameterMode.IN);
        query.setParameter("p_staff_id", staffId);
        return map(query.getResultList());
    }

    public void addItem(Long staffId, Long productId, Integer quantity) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_cart_add_item");
        query.registerStoredProcedureParameter("p_staff_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_product_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
        query.setParameter("p_staff_id", staffId);
        query.setParameter("p_product_id", productId);
        query.setParameter("p_quantity", quantity);
        query.execute();
    }

    public void clearByStaffId(Long staffId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_cart_clear_by_staff");
        query.registerStoredProcedureParameter("p_staff_id", Long.class, ParameterMode.IN);
        query.setParameter("p_staff_id", staffId);
        query.execute();
    }

    private List<CartItemView> map(List<?> rows) {
        return rows.stream()
                .map(row -> {
                    Object[] data = (Object[]) row;
                    return new CartItemView(
                            ((Number) data[0]).longValue(),
                            ((Number) data[1]).longValue(),
                            ((Number) data[2]).longValue(),
                            (String) data[3],
                            (String) data[4],
                            ((Number) data[5]).intValue(),
                            ((Number) data[6]).intValue(),
                            ((Number) data[7]).intValue()
                    );
                })
                .toList();
    }
}
