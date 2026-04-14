package com.tdtu.coreapi.order;

import com.tdtu.coreapi.order.dto.CreateOrderRequest;
import com.tdtu.coreapi.order.dto.OnlineOrderSummaryView;
import com.tdtu.coreapi.order.dto.OrderCreatedView;
import com.tdtu.coreapi.order.dto.OrderDetailView;
import com.tdtu.coreapi.order.dto.OrderSummaryView;
import com.tdtu.coreapi.order.dto.UpdateOrderStatusRequest;
import com.tdtu.coreapi.voucher.VoucherProcedureRepository;
import com.tdtu.coreapi.voucher.dto.VoucherValidationView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderProcedureRepository orderProcedureRepository;
    private final VoucherProcedureRepository voucherProcedureRepository;

    public OrderService(OrderProcedureRepository orderProcedureRepository,
                        VoucherProcedureRepository voucherProcedureRepository) {
        this.orderProcedureRepository = orderProcedureRepository;
        this.voucherProcedureRepository = voucherProcedureRepository;
    }

    @Transactional
    public OrderCreatedView createOrder(CreateOrderRequest request) {
        int subtotalMoney = request.items().stream()
                .mapToInt(item -> item.quantity() * item.unitPrice())
                .sum();
        int shippingFee = request.shippingFee() == null ? 0 : request.shippingFee();
        VoucherValidationView voucher = null;
        int discountMoney = 0;
        if (request.voucherCode() != null && !request.voucherCode().isBlank()) {
            voucher = voucherProcedureRepository.validatePublic(request.voucherCode().trim().toUpperCase(), subtotalMoney, shippingFee);
            discountMoney = voucher.discountMoney();
        }
        int totalMoney = Math.max(subtotalMoney + shippingFee - discountMoney, 0);

        Long customerId = orderProcedureRepository.ensureCustomer(
                request.phoneNumber(),
                request.fullName(),
                request.shippingAddress()
        );

        OrderCreatedView order = orderProcedureRepository.createOrder(
                request,
                customerId,
                subtotalMoney,
                discountMoney,
                totalMoney,
                voucher != null ? voucher.voucherId() : null,
                voucher != null ? voucher.voucherCode() : null,
                voucher != null ? voucher.voucherName() : null,
                voucher != null ? voucher.voucherType() : null,
                voucher != null ? voucher.discountValue() : null,
                voucher != null ? voucher.minOrderValue() : null
        );
        request.items().forEach(item -> orderProcedureRepository.addItem(order.orderId(), item));
        if (voucher != null) {
            voucherProcedureRepository.consumeForOrder(order.orderId(), voucher.voucherCode(), subtotalMoney, shippingFee);
        }
        return order;
    }

    public List<OrderSummaryView> getOrdersByPhone(String phoneNumber) {
        return orderProcedureRepository.getOrdersByPhone(phoneNumber);
    }

    public List<OnlineOrderSummaryView> getAllOrders() {
        return orderProcedureRepository.getAllOrders();
    }

    public List<OrderDetailView> getOrderDetail(String orderCode) {
        return orderProcedureRepository.getOrderDetail(orderCode);
    }

    @Transactional
    public void updateStatus(UpdateOrderStatusRequest request) {
        orderProcedureRepository.updateStatus(request.orderId(), request.orderStatus(), request.paymentStatus());
    }
}
