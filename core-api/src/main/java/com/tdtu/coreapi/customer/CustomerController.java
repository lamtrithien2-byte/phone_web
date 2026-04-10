package com.tdtu.coreapi.customer;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.customer.dto.CustomerView;
import com.tdtu.coreapi.customer.dto.PurchaseHistoryView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/by-phone")
    public ApiResponse<CustomerView> findByPhone(@RequestParam String phoneNumber) {
        CustomerView customer = customerService.findByPhoneNumber(phoneNumber);
        if (customer == null) {
            return ApiResponse.error("Customer not found");
        }
        return ApiResponse.success(customer);
    }

    @GetMapping("/{customerId}/purchase-history")
    public ApiResponse<List<PurchaseHistoryView>> purchaseHistory(@PathVariable Long customerId) {
        return ApiResponse.success(customerService.getPurchaseHistory(customerId));
    }
}
