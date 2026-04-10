package com.tdtu.coreapi.order;

import com.tdtu.coreapi.order.dto.CreateOrderItemRequest;
import com.tdtu.coreapi.order.dto.CreateOrderRequest;
import com.tdtu.coreapi.order.dto.OnlineOrderSummaryView;
import com.tdtu.coreapi.order.dto.OrderCreatedView;
import com.tdtu.coreapi.order.dto.OrderDetailView;
import com.tdtu.coreapi.order.dto.OrderSummaryView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Long ensureCustomer(String phoneNumber, String fullName, String address) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_customer_create_if_not_exists");
        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_full_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_address", String.class, ParameterMode.IN);
        query.setParameter("p_phone_number", phoneNumber);
        query.setParameter("p_full_name", fullName);
        query.setParameter("p_address", address);
        return ((Number) query.getSingleResult()).longValue();
    }

    public OrderCreatedView createOrder(CreateOrderRequest request, Long customerId, int subtotalMoney, int totalMoney) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_order_create");
        query.registerStoredProcedureParameter("p_order_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_subtotal_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_shipping_fee", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_discount_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_total_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_recipient_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_recipient_phone", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_shipping_address", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_note", String.class, ParameterMode.IN);

        String orderCode = "ORD-" + System.currentTimeMillis();
        query.setParameter("p_order_code", orderCode);
        query.setParameter("p_customer_id", customerId);
        query.setParameter("p_subtotal_money", subtotalMoney);
        query.setParameter("p_shipping_fee", request.shippingFee() == null ? 0 : request.shippingFee());
        query.setParameter("p_discount_money", 0);
        query.setParameter("p_total_money", totalMoney);
        query.setParameter("p_recipient_name", request.fullName());
        query.setParameter("p_recipient_phone", request.phoneNumber());
        query.setParameter("p_shipping_address", request.shippingAddress());
        query.setParameter("p_note", request.note());

        Long orderId = ((Number) query.getSingleResult()).longValue();
        return new OrderCreatedView(orderId, orderCode, customerId, totalMoney);
    }

    public void addItem(Long orderId, CreateOrderItemRequest item) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_order_add_item");
        query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_product_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_unit_price", Integer.class, ParameterMode.IN);
        query.setParameter("p_order_id", orderId);
        query.setParameter("p_product_id", item.productId());
        query.setParameter("p_quantity", item.quantity());
        query.setParameter("p_unit_price", item.unitPrice());
        query.execute();
    }

    public List<OrderSummaryView> getOrdersByPhone(String phoneNumber) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_order_get_by_customer_phone");
        query.registerStoredProcedureParameter("p_phone_number", String.class, ParameterMode.IN);
        query.setParameter("p_phone_number", phoneNumber);
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new OrderSummaryView(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            (String) row[4],
                            ((Number) row[5]).intValue(),
                            (java.util.Date) row[6]
                    );
                })
                .toList();
    }

    public List<OnlineOrderSummaryView> getAllOrders() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_order_get_all");
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new OnlineOrderSummaryView(
                            ((Number) row[0]).longValue(),
                            (String) row[1],
                            (String) row[2],
                            (String) row[3],
                            (String) row[4],
                            (String) row[5],
                            (String) row[6],
                            ((Number) row[7]).intValue(),
                            (java.util.Date) row[8]
                    );
                })
                .toList();
    }

    public List<OrderDetailView> getOrderDetail(String orderCode) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_order_get_detail");
        query.registerStoredProcedureParameter("p_order_code", String.class, ParameterMode.IN);
        query.setParameter("p_order_code", orderCode);
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new OrderDetailView(
                            (String) row[0],
                            (String) row[1],
                            (String) row[2],
                            ((Number) row[3]).intValue(),
                            (String) row[4],
                            (String) row[5],
                            ((Number) row[6]).intValue(),
                            ((Number) row[7]).intValue(),
                            ((Number) row[8]).intValue(),
                            (java.util.Date) row[9]
                    );
                })
                .toList();
    }

    public void updateStatus(Long orderId, String orderStatus, String paymentStatus) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_order_update_status");
        query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_order_status", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_payment_status", String.class, ParameterMode.IN);
        query.setParameter("p_order_id", orderId);
        query.setParameter("p_order_status", orderStatus);
        query.setParameter("p_payment_status", paymentStatus);
        query.getSingleResult();
    }
}
