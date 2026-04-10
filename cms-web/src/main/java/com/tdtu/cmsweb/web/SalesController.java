package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.ApiResponse;
import com.tdtu.cmsweb.api.CartApiClient;
import com.tdtu.cmsweb.api.CustomerApiClient;
import com.tdtu.cmsweb.api.InvoiceApiClient;
import com.tdtu.cmsweb.api.OrderApiClient;
import com.tdtu.cmsweb.api.ProductApiClient;
import com.tdtu.cmsweb.api.dto.AddCartItemRequest;
import com.tdtu.cmsweb.api.dto.CartSummaryView;
import com.tdtu.cmsweb.api.dto.CreateInvoiceRequest;
import com.tdtu.cmsweb.api.dto.CustomerView;
import com.tdtu.cmsweb.api.dto.InvoiceCreatedView;
import com.tdtu.cmsweb.api.dto.InvoiceDetailView;
import com.tdtu.cmsweb.api.dto.InvoiceItemRequest;
import com.tdtu.cmsweb.api.dto.LoginResult;
import com.tdtu.cmsweb.api.dto.OnlineOrderSummaryView;
import com.tdtu.cmsweb.api.dto.ProductView;
import com.tdtu.cmsweb.api.dto.PurchaseHistoryView;
import com.tdtu.cmsweb.api.dto.UpdateOrderStatusRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
public class SalesController {

    private final ProductApiClient productApiClient;
    private final CartApiClient cartApiClient;
    private final CustomerApiClient customerApiClient;
    private final InvoiceApiClient invoiceApiClient;
    private final OrderApiClient orderApiClient;

    public SalesController(ProductApiClient productApiClient,
                           CartApiClient cartApiClient,
                           CustomerApiClient customerApiClient,
                           InvoiceApiClient invoiceApiClient,
                           OrderApiClient orderApiClient) {
        this.productApiClient = productApiClient;
        this.cartApiClient = cartApiClient;
        this.customerApiClient = customerApiClient;
        this.invoiceApiClient = invoiceApiClient;
        this.orderApiClient = orderApiClient;
    }

    @GetMapping("/sales")
    public String salesPage(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String phoneNumber,
                            @RequestParam(required = false) String invoiceCode,
                            @RequestParam(required = false) String onlineOrderCode,
                            Model model,
                            HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Long staffId = currentUser.id();
        bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, null);
        SessionSupport.bindCurrentUser(model, session);
        return "sales";
    }

    @PostMapping("/sales/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String phoneNumber,
                            @RequestParam(required = false) String invoiceCode,
                            @RequestParam(required = false) String onlineOrderCode,
                            Model model,
                            HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Long staffId = currentUser.id();
        try {
            cartApiClient.addItem(new AddCartItemRequest(staffId, productId, quantity));
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, "Da them vao cart", null);
            SessionSupport.bindCurrentUser(model, session);
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, "Core API is unavailable");
            SessionSupport.bindCurrentUser(model, session);
        }
        return "sales";
    }

    @PostMapping("/sales/checkout")
    public String checkout(@RequestParam String phoneNumber,
                           @RequestParam String fullName,
                           @RequestParam String address,
                           @RequestParam int receiveMoney,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String invoiceCode,
                           @RequestParam(required = false) String onlineOrderCode,
                           Model model,
                           HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Long staffId = currentUser.id();
        try {
            CartSummaryView cart = cartApiClient.getCart(staffId);
            if (cart == null || cart.items() == null || cart.items().isEmpty()) {
                bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, "Cart is empty");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }

            List<InvoiceItemRequest> items = cart.items().stream()
                    .map(item -> new InvoiceItemRequest(item.productId(), item.quantity(), item.totalMoney()))
                    .toList();

            int totalAmount = cart.totalAmount() != null ? cart.totalAmount() : 0;
            int totalQuantity = cart.totalQuantity() != null ? cart.totalQuantity() : 0;
            int moneyBack = Math.max(receiveMoney - totalAmount, 0);

            var response = invoiceApiClient.create(new CreateInvoiceRequest(
                    staffId,
                    phoneNumber,
                    fullName,
                    address,
                    totalQuantity,
                    totalAmount,
                    receiveMoney,
                    moneyBack,
                    items
            ));

            InvoiceCreatedView invoice = response != null ? response.data() : null;
            if (response == null || !response.isSuccess() || invoice == null) {
                bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, response != null ? response.message() : "Unable to create invoice");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }

            bindSalesPage(model, staffId, keyword, phoneNumber, invoice.invoiceCode(), onlineOrderCode,
                    "Da tao hoa don " + invoice.invoiceCode(),
                    null);
            model.addAttribute("createdInvoice", invoice);
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, onlineOrderCode, null, "Core API is unavailable");
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        }
    }

    @PostMapping("/sales/orders/pay")
    public String confirmOnlineOrderPayment(@RequestParam Long orderId,
                                            @RequestParam String orderCode,
                                            @RequestParam int receiveMoney,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String phoneNumber,
                                            @RequestParam(required = false) String invoiceCode,
                                            Model model,
                                            HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Long staffId = currentUser.id();
        try {
            List<OnlineOrderSummaryView> orders = orderApiClient.getAllOrders();
            OnlineOrderSummaryView selectedOrder = orders.stream()
                    .filter(order -> order.id().equals(orderId))
                    .findFirst()
                    .orElse(null);

            if (selectedOrder == null) {
                bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, orderCode, null, "Khong tim thay don online");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }
            if (receiveMoney < selectedOrder.totalMoney()) {
                bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, orderCode, null, "Tien nhan chua du de thanh toan");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }

            ApiResponse<Void> response = orderApiClient.updateStatus(new UpdateOrderStatusRequest(
                    orderId,
                    "COMPLETED",
                    "PAID"
            ));

            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, orderCode,
                    response != null && response.isSuccess()
                            ? "Da xac nhan thanh toan don " + orderCode + ". Tien thua: " + (receiveMoney - selectedOrder.totalMoney()) + " VND"
                            : null,
                    response == null || response.isSuccess() ? null : response.message());
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, orderCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, phoneNumber, invoiceCode, orderCode, null, "Core API is unavailable");
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        }
    }

    private void bindSalesPage(Model model,
                               Long staffId,
                               String keyword,
                               String phoneNumber,
                               String invoiceCode,
                               String onlineOrderCode,
                               String success,
                               String error) {
        model.addAttribute("staffId", staffId);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("phoneNumber", phoneNumber == null ? "" : phoneNumber);
        model.addAttribute("invoiceCode", invoiceCode == null ? "" : invoiceCode);
        model.addAttribute("onlineOrderCode", onlineOrderCode == null ? "" : onlineOrderCode);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("shopUrl", "http://127.0.0.1:8083/products");

        try {
            model.addAttribute("products", productApiClient.getProducts(keyword));
            model.addAttribute("cart", cartApiClient.getCart(staffId));
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                var customerResponse = customerApiClient.findByPhone(phoneNumber);
                CustomerView customer = customerResponse != null ? customerResponse.data() : null;
                model.addAttribute("customer", customer);
                model.addAttribute("customerHistory", customer != null
                        ? customerApiClient.getPurchaseHistory(customer.id())
                        : List.<PurchaseHistoryView>of());
            } else {
                model.addAttribute("customer", null);
                model.addAttribute("customerHistory", List.<PurchaseHistoryView>of());
            }
            model.addAttribute("invoiceDetails",
                    invoiceCode != null && !invoiceCode.isBlank()
                            ? invoiceApiClient.getDetails(invoiceCode)
                            : List.<InvoiceDetailView>of());
            List<OnlineOrderSummaryView> onlineOrders = orderApiClient.getAllOrders();
            model.addAttribute("onlineOrders", onlineOrders);
            OnlineOrderSummaryView selectedOnlineOrder = onlineOrderCode != null && !onlineOrderCode.isBlank()
                    ? onlineOrders.stream()
                    .filter(order -> order.orderCode().equalsIgnoreCase(onlineOrderCode))
                    .findFirst()
                    .orElse(null)
                    : null;
            model.addAttribute("selectedOnlineOrder", selectedOnlineOrder);
            model.addAttribute("onlineOrderDetails",
                    onlineOrderCode != null && !onlineOrderCode.isBlank()
                            ? orderApiClient.getDetails(onlineOrderCode)
                            : List.<com.tdtu.cmsweb.api.dto.OrderDetailView>of());
        } catch (RestClientException ex) {
            model.addAttribute("products", List.<ProductView>of());
            model.addAttribute("cart", null);
            model.addAttribute("customer", null);
            model.addAttribute("customerHistory", List.<PurchaseHistoryView>of());
            model.addAttribute("invoiceDetails", List.<InvoiceDetailView>of());
            model.addAttribute("onlineOrders", List.<OnlineOrderSummaryView>of());
            model.addAttribute("selectedOnlineOrder", null);
            model.addAttribute("onlineOrderDetails", List.<com.tdtu.cmsweb.api.dto.OrderDetailView>of());
            if (error == null) {
                model.addAttribute("error", "Core API is unavailable");
            }
        }
    }

    private String extractApiError(HttpStatusCodeException ex) {
        String body = ex.getResponseBodyAsString();
        if (body != null && body.contains("\"message\"")) {
            int keyIndex = body.indexOf("\"message\"");
            int colonIndex = body.indexOf(':', keyIndex);
            int firstQuote = body.indexOf('"', colonIndex + 1);
            int secondQuote = body.indexOf('"', firstQuote + 1);
            if (firstQuote >= 0 && secondQuote > firstQuote) {
                return body.substring(firstQuote + 1, secondQuote);
            }
        }
        return "Yeu cau tao hoa don khong hop le";
    }
}
