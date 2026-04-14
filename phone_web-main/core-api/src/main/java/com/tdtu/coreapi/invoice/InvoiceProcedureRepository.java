package com.tdtu.coreapi.invoice;

import com.tdtu.coreapi.invoice.dto.CreateInvoiceRequest;
import com.tdtu.coreapi.invoice.dto.InvoiceCreatedView;
import com.tdtu.coreapi.invoice.dto.InvoiceDetailView;
import com.tdtu.coreapi.invoice.dto.InvoiceItemRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvoiceProcedureRepository {

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

        Object row = query.getSingleResult();
        return ((Number) row).longValue();
    }

    public InvoiceCreatedView createInvoice(CreateInvoiceRequest request, Long customerId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_invoice_create");
        query.registerStoredProcedureParameter("p_invoice_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_customer_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_user_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_receive_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_excess_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_subtotal_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_discount_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_total_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_type", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_value", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_min_order_value", Integer.class, ParameterMode.IN);

        String invoiceCode = "INV-" + System.currentTimeMillis();
        query.setParameter("p_invoice_code", invoiceCode);
        query.setParameter("p_customer_id", customerId);
        query.setParameter("p_user_id", request.staffId());
        query.setParameter("p_receive_money", request.receiveMoney());
        query.setParameter("p_excess_money", request.moneyBack());
        query.setParameter("p_subtotal_money", request.subtotalMoney());
        query.setParameter("p_discount_money", request.discountMoney());
        query.setParameter("p_total_money", request.totalMoney());
        query.setParameter("p_quantity", request.quantity());
        query.setParameter("p_voucher_id", null);
        query.setParameter("p_voucher_code", request.voucherCode());
        query.setParameter("p_voucher_name", null);
        query.setParameter("p_voucher_type", null);
        query.setParameter("p_voucher_value", null);
        query.setParameter("p_voucher_min_order_value", null);

        Object result = query.getSingleResult();
        Long invoiceId = ((Number) result).longValue();
        return new InvoiceCreatedView(
                invoiceId,
                invoiceCode,
                customerId,
                null,
                request.subtotalMoney(),
                request.discountMoney(),
                request.totalMoney(),
                request.voucherCode()
        );
    }

    public void addInvoiceItem(Long invoiceId, InvoiceItemRequest item) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_invoice_item_create");
        query.registerStoredProcedureParameter("p_invoice_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_product_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_unit_price", Integer.class, ParameterMode.IN);
        query.setParameter("p_invoice_id", invoiceId);
        query.setParameter("p_product_id", item.productId());
        query.setParameter("p_quantity", item.quantity());
        query.setParameter("p_unit_price", item.totalMoney() / Math.max(1, item.quantity()));
        query.execute();
    }

    public void increaseProductSaleNumber(Long productId, Integer quantity) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_product_increase_sale_number");
        query.registerStoredProcedureParameter("p_product_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quantity", Integer.class, ParameterMode.IN);
        query.setParameter("p_product_id", productId);
        query.setParameter("p_quantity", quantity);
        query.execute();
    }

    public void completeCheckout(Long staffId) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_invoice_complete_checkout");
        query.registerStoredProcedureParameter("p_staff_id", Long.class, ParameterMode.IN);
        query.setParameter("p_staff_id", staffId);
        query.execute();
    }

    public void attachPdf(String invoiceCode, String pdfLink) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_invoice_attach_pdf");
        query.registerStoredProcedureParameter("p_invoice_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pdf_link", String.class, ParameterMode.IN);
        query.setParameter("p_invoice_code", invoiceCode);
        query.setParameter("p_pdf_link", pdfLink);
        query.execute();
    }

    public void consumeVoucherForInvoice(Long invoiceId, String voucherCode, int subtotalMoney, int shippingFee) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_voucher_consume_for_invoice");
        query.registerStoredProcedureParameter("p_invoice_id", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_voucher_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_subtotal_money", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_shipping_fee", Integer.class, ParameterMode.IN);
        query.setParameter("p_invoice_id", invoiceId);
        query.setParameter("p_voucher_code", voucherCode);
        query.setParameter("p_subtotal_money", subtotalMoney);
        query.setParameter("p_shipping_fee", shippingFee);
        query.getSingleResult();
    }

    public List<InvoiceDetailView> getInvoiceDetail(String invoiceCode) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_invoice_get_detail");
        query.registerStoredProcedureParameter("p_invoice_code", String.class, ParameterMode.IN);
        query.setParameter("p_invoice_code", invoiceCode);
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new InvoiceDetailView(
                            (String) row[0],
                            ((Number) row[1]).intValue(),
                            ((Number) row[2]).intValue(),
                            ((Number) row[3]).intValue(),
                            (String) row[4],
                            (String) row[5],
                            ((Number) row[6]).intValue(),
                            ((Number) row[7]).intValue(),
                            (java.util.Date) row[8]
                    );
                })
                .toList();
    }
}
