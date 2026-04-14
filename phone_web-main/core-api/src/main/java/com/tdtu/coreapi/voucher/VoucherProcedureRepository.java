package com.tdtu.coreapi.voucher;

import com.tdtu.coreapi.common.BusinessException;
import com.tdtu.coreapi.voucher.dto.VoucherUpsertRequest;
import com.tdtu.coreapi.voucher.dto.VoucherValidationView;
import com.tdtu.coreapi.voucher.dto.VoucherView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class VoucherProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<VoucherView> getAll(String keyword, String status, String voucherType, Boolean expiredOnly) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_get_all");
        query.registerStoredProcedureParameter("p_keyword", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_status", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_type", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_expired_only", Boolean.class, ParameterMode.IN);
        query.setParameter("p_keyword", keyword);
        query.setParameter("p_status", status);
        query.setParameter("p_voucher_type", voucherType);
        query.setParameter("p_expired_only", expiredOnly);
        return query.getResultList().stream()
                .map(this::mapVoucherRow)
                .toList();
    }

    public VoucherView getById(Long voucherId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_get_by_id");
        query.registerStoredProcedureParameter("p_voucher_id", Long.class, ParameterMode.IN);
        query.setParameter("p_voucher_id", voucherId);
        List<?> rows = query.getResultList();
        if (rows.isEmpty()) {
            throw new BusinessException("Voucher not found.");
        }
        Object[] row = (Object[]) rows.get(0);
        return new VoucherView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                ((Number) row[4]).intValue(),
                (Boolean) row[5],
                toLocalDateTime(row[6]),
                toLocalDateTime(row[7]),
                ((Number) row[8]).intValue(),
                ((Number) row[9]).intValue(),
                ((Number) row[10]).intValue(),
                null
        );
    }

    public Long create(VoucherUpsertRequest request) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_create");
        bindUpsertParameters(query, request);
        return ((Number) query.getSingleResult()).longValue();
    }

    public void update(Long voucherId, VoucherUpsertRequest request) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_update");
        query.registerStoredProcedureParameter("p_voucher_id", Long.class, ParameterMode.IN);
        query.setParameter("p_voucher_id", voucherId);
        bindUpsertParameters(query, request);
        query.getSingleResult();
    }

    public void delete(Long voucherId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_delete");
        query.registerStoredProcedureParameter("p_voucher_id", Long.class, ParameterMode.IN);
        query.setParameter("p_voucher_id", voucherId);
        query.getSingleResult();
    }

    public void toggle(Long voucherId, boolean active) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_toggle");
        query.registerStoredProcedureParameter("p_voucher_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_is_active", Boolean.class, ParameterMode.IN);
        query.setParameter("p_voucher_id", voucherId);
        query.setParameter("p_is_active", active);
        query.getSingleResult();
    }

    public VoucherValidationView validatePublic(String voucherCode, int subtotalMoney, int shippingFee) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_validate_public");
        query.registerStoredProcedureParameter("p_voucher_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_subtotal_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_shipping_fee", Integer.class, ParameterMode.IN);
        query.setParameter("p_voucher_code", voucherCode);
        query.setParameter("p_subtotal_money", subtotalMoney);
        query.setParameter("p_shipping_fee", shippingFee);
        Object[] row = (Object[]) query.getSingleResult();
        return new VoucherValidationView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                ((Number) row[4]).intValue(),
                ((Number) row[5]).intValue(),
                ((Number) row[6]).intValue(),
                ((Number) row[7]).intValue(),
                ((Number) row[8]).intValue()
        );
    }

    public void consumeForOrder(Long orderId, String voucherCode, int subtotalMoney, int shippingFee) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_consume_for_order");
        query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_subtotal_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_shipping_fee", Integer.class, ParameterMode.IN);
        query.setParameter("p_order_id", orderId);
        query.setParameter("p_voucher_code", voucherCode);
        query.setParameter("p_subtotal_money", subtotalMoney);
        query.setParameter("p_shipping_fee", shippingFee);
        query.getSingleResult();
    }

    public void releaseForOrder(Long orderId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_release_for_order");
        query.registerStoredProcedureParameter("p_order_id", Long.class, ParameterMode.IN);
        query.setParameter("p_order_id", orderId);
        query.getSingleResult();
    }

    private void bindUpsertParameters(StoredProcedureQuery query, VoucherUpsertRequest request) {
        query.registerStoredProcedureParameter("p_voucher_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_type", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_discount_value", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_is_active", Boolean.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_starts_at", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_ends_at", LocalDateTime.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_max_usage", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_min_order_value", Integer.class, ParameterMode.IN);
        query.setParameter("p_voucher_code", request.voucherCode());
        query.setParameter("p_voucher_name", request.voucherName());
        query.setParameter("p_voucher_type", request.voucherType());
        query.setParameter("p_discount_value", request.discountValue());
        query.setParameter("p_is_active", request.active());
        query.setParameter("p_starts_at", request.startsAt());
        query.setParameter("p_ends_at", request.endsAt());
        query.setParameter("p_max_usage", request.maxUsage());
        query.setParameter("p_min_order_value", request.minOrderValue());
    }

    private VoucherView mapVoucherRow(Object value) {
        Object[] row = (Object[]) value;
        return new VoucherView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                ((Number) row[4]).intValue(),
                (Boolean) row[5],
                toLocalDateTime(row[6]),
                toLocalDateTime(row[7]),
                ((Number) row[8]).intValue(),
                ((Number) row[9]).intValue(),
                ((Number) row[10]).intValue(),
                (String) row[11]
        );
    }

    private LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }
        return value instanceof LocalDateTime localDateTime ? localDateTime : null;
    }
}
