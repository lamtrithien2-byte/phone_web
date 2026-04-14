package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.CustomerApiClient;
import com.tdtu.cmsweb.api.InvoiceApiClient;
import com.tdtu.cmsweb.api.OrderApiClient;
import com.tdtu.cmsweb.api.dto.CustomerView;
import com.tdtu.cmsweb.api.dto.InvoiceDetailView;
import com.tdtu.cmsweb.api.dto.LoginResult;
import com.tdtu.cmsweb.api.dto.OrderDetailView;
import com.tdtu.cmsweb.api.dto.PurchaseHistoryView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
public class CustomerLookupController {

    private final CustomerApiClient customerApiClient;
    private final InvoiceApiClient invoiceApiClient;
    private final OrderApiClient orderApiClient;

    public CustomerLookupController(CustomerApiClient customerApiClient,
                                    InvoiceApiClient invoiceApiClient,
                                    OrderApiClient orderApiClient) {
        this.customerApiClient = customerApiClient;
        this.invoiceApiClient = invoiceApiClient;
        this.orderApiClient = orderApiClient;
    }

    @GetMapping("/customers/lookup")
    public String lookup(@RequestParam(required = false) String phoneNumber,
                         @RequestParam(required = false) String recordCode,
                         Model model,
                         HttpSession session) {
        if (SessionSupport.requireLogin(session, model)) {
            return "redirect:/login";
        }
        bind(model, session, phoneNumber, recordCode, null);
        return "customer-lookup";
    }

    private void bind(Model model,
                      HttpSession session,
                      String phoneNumber,
                      String recordCode,
                      String error) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        SessionSupport.bindCurrentUser(model, session);
        model.addAttribute("activePage", "customerLookup");
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("productManagementUrl", "/products/manage");
        model.addAttribute("voucherManagementUrl", "/vouchers");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("customerLookupUrl", "/customers/lookup");
        model.addAttribute("shopUrl", "http://localhost:8083/products");
        model.addAttribute("isAdmin", currentUser != null && "admin".equalsIgnoreCase(currentUser.roleName()));
        model.addAttribute("phoneNumber", phoneNumber == null ? "" : phoneNumber);
        model.addAttribute("recordCode", recordCode == null ? "" : recordCode);

        try {
            CustomerView customer = null;
            List<PurchaseHistoryView> history = List.of();
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                var customerResponse = customerApiClient.findByPhone(phoneNumber);
                customer = customerResponse != null ? customerResponse.data() : null;
                history = customer != null ? customerApiClient.getPurchaseHistory(customer.id()) : List.of();
            }

            PurchaseHistoryView selectedHistory = recordCode != null && !recordCode.isBlank()
                    ? history.stream()
                    .filter(item -> recordCode.equalsIgnoreCase(item.invoiceCode()))
                    .findFirst()
                    .orElse(null)
                    : (!history.isEmpty() ? history.get(0) : null);

            List<InvoiceDetailView> invoiceDetails = List.of();
            List<OrderDetailView> orderDetails = List.of();
            if (recordCode != null && !recordCode.isBlank()) {
                if (recordCode.regionMatches(true, 0, "ORD-", 0, 4)) {
                    orderDetails = orderApiClient.getDetails(recordCode);
                } else {
                    invoiceDetails = invoiceApiClient.getDetails(recordCode);
                }
            }

            model.addAttribute("customer", customer);
            model.addAttribute("customerHistory", history);
            model.addAttribute("selectedHistory", selectedHistory);
            model.addAttribute("invoiceDetails", invoiceDetails);
            model.addAttribute("orderDetails", orderDetails);
            model.addAttribute("error", error);
        } catch (RestClientException ex) {
            model.addAttribute("customer", null);
            model.addAttribute("customerHistory", List.<PurchaseHistoryView>of());
            model.addAttribute("selectedHistory", null);
            model.addAttribute("invoiceDetails", List.<InvoiceDetailView>of());
            model.addAttribute("orderDetails", List.<OrderDetailView>of());
            model.addAttribute("error", error != null ? error : "H? th?ng x? l? d? li?u ?ang t?m th?i kh?ng ph?n h?i.");
        }
    }
}

