package com.tdtu.coreapi.order;

import com.tdtu.coreapi.order.dto.CreateOrderRequest;
import com.tdtu.coreapi.order.dto.OnlineOrderSummaryView;
import com.tdtu.coreapi.order.dto.OrderCreatedView;
import com.tdtu.coreapi.order.dto.OrderDetailView;
import com.tdtu.coreapi.order.dto.OrderSummaryView;
import com.tdtu.coreapi.order.dto.UpdateOrderStatusRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderProcedureRepository orderProcedureRepository;

    public OrderService(OrderProcedureRepository orderProcedureRepository) {
        this.orderProcedureRepository = orderProcedureRepository;
    }

    @Transactional
    public OrderCreatedView createOrder(CreateOrderRequest request) {
        int subtotalMoney = request.items().stream()
                .mapToInt(item -> item.quantity() * item.unitPrice())
                .sum();
        int totalMoney = subtotalMoney + (request.shippingFee() == null ? 0 : request.shippingFee());

        Long customerId = orderProcedureRepository.ensureCustomer(
                request.phoneNumber(),
                request.fullName(),
                request.shippingAddress()
        );

        OrderCreatedView order = orderProcedureRepository.createOrder(request, customerId, subtotalMoney, totalMoney);
        request.items().forEach(item -> orderProcedureRepository.addItem(order.orderId(), item));
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
