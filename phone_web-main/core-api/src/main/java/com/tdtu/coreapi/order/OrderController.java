package com.tdtu.coreapi.order;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.common.BusinessException;
import com.tdtu.coreapi.order.dto.CreateOrderRequest;
import com.tdtu.coreapi.order.dto.OnlineOrderSummaryView;
import com.tdtu.coreapi.order.dto.OrderCreatedView;
import com.tdtu.coreapi.order.dto.OrderDetailView;
import com.tdtu.coreapi.order.dto.OrderSummaryView;
import com.tdtu.coreapi.order.dto.UpdateOrderStatusRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderCreatedView> create(@Valid @RequestBody CreateOrderRequest request) {
        try {
            return ApiResponse.success(orderService.createOrder(request));
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }

    @GetMapping
    public ApiResponse<List<OnlineOrderSummaryView>> getAll() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    @GetMapping("/by-phone")
    public ApiResponse<List<OrderSummaryView>> getByPhone(@RequestParam String phoneNumber) {
        return ApiResponse.success(orderService.getOrdersByPhone(phoneNumber));
    }

    @GetMapping("/{orderCode}")
    public ApiResponse<List<OrderDetailView>> getDetail(@PathVariable String orderCode) {
        return ApiResponse.success(orderService.getOrderDetail(orderCode));
    }

    @PutMapping("/status")
    public ApiResponse<Void> updateStatus(@Valid @RequestBody UpdateOrderStatusRequest request) {
        try {
            orderService.updateStatus(request);
            return ApiResponse.success(null);
        } catch (BusinessException ex) {
            return ApiResponse.error(ex.getMessage());
        }
    }
}
