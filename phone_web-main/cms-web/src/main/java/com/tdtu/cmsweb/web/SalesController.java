package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.ApiResponse;
import com.tdtu.cmsweb.api.CartApiClient;
import com.tdtu.cmsweb.api.InvoiceApiClient;
import com.tdtu.cmsweb.api.OrderApiClient;
import com.tdtu.cmsweb.api.ProductApiClient;
import com.tdtu.cmsweb.api.VoucherApiClient;
import com.tdtu.cmsweb.api.dto.AddCartItemRequest;
import com.tdtu.cmsweb.api.dto.CartSummaryView;
import com.tdtu.cmsweb.api.dto.CreateInvoiceRequest;
import com.tdtu.cmsweb.api.dto.InvoiceCreatedView;
import com.tdtu.cmsweb.api.dto.InvoiceDetailView;
import com.tdtu.cmsweb.api.dto.InvoiceItemRequest;
import com.tdtu.cmsweb.api.dto.LoginResult;
import com.tdtu.cmsweb.api.dto.OnlineOrderSummaryView;
import com.tdtu.cmsweb.api.dto.ProductView;
import com.tdtu.cmsweb.api.dto.UpdateOrderStatusRequest;
import com.tdtu.cmsweb.api.dto.VoucherValidationView;
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
    private final InvoiceApiClient invoiceApiClient;
    private final OrderApiClient orderApiClient;
    private final VoucherApiClient voucherApiClient;

    public SalesController(ProductApiClient productApiClient,
                           CartApiClient cartApiClient,
                           InvoiceApiClient invoiceApiClient,
                           OrderApiClient orderApiClient,
                           VoucherApiClient voucherApiClient) {
        this.productApiClient = productApiClient;
        this.cartApiClient = cartApiClient;
        this.invoiceApiClient = invoiceApiClient;
        this.orderApiClient = orderApiClient;
        this.voucherApiClient = voucherApiClient;
    }

    @GetMapping("/sales")
    public String salesPage(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String invoiceCode,
                            @RequestParam(required = false) String onlineOrderCode,
                            @RequestParam(required = false) String voucherCode,
                            Model model,
                            HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        bindSalesPage(model, currentUser.id(), keyword, invoiceCode, onlineOrderCode, voucherCode, null, null);
        SessionSupport.bindCurrentUser(model, session);
        return "sales";
    }

    @PostMapping("/sales/cart/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String invoiceCode,
                            @RequestParam(required = false) String onlineOrderCode,
                            @RequestParam(required = false) String voucherCode,
                            Model model,
                            HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Long staffId = currentUser.id();
        try {
            cartApiClient.addItem(new AddCartItemRequest(staffId, productId, quantity));
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, "ÄÃ£ thÃªm sáº£n pháº©m vÃ o giá» bÃ¡n hÃ ng.", null);
            SessionSupport.bindCurrentUser(model, session);
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            SessionSupport.bindCurrentUser(model, session);
        }
        return "sales";
    }

    @PostMapping("/sales/cart/remove")
    public String removeCartItem(@RequestParam Long cartItemId,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String invoiceCode,
                                 @RequestParam(required = false) String onlineOrderCode,
                                 @RequestParam(required = false) String voucherCode,
                                 Model model,
                                 HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        Long staffId = currentUser.id();
        try {
            cartApiClient.deleteItem(staffId, cartItemId);
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, "ÄÃ£ xÃ³a sáº£n pháº©m khá»i giá» hÃ ng.", null);
            SessionSupport.bindCurrentUser(model, session);
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            SessionSupport.bindCurrentUser(model, session);
        }
        return "sales";
    }

    @PostMapping("/sales/cart/clear")
    public String clearCart(@RequestParam(required = false) String keyword,
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
            cartApiClient.clearCart(staffId);
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, "ÄÃ£ lÃ m má»›i giá» hÃ ng.", null);
            SessionSupport.bindCurrentUser(model, session);
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            SessionSupport.bindCurrentUser(model, session);
        }
        return "sales";
    }

    @PostMapping("/sales/voucher/apply")
    public String applyVoucher(@RequestParam String voucherCode,
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
                bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null, "Giá» bÃ¡n hÃ ng Ä‘ang trá»‘ng.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }
            int subtotalMoney = cart.totalAmount() == null ? 0 : cart.totalAmount();
            ApiResponse<VoucherValidationView> response = voucherApiClient.validate(voucherCode, subtotalMoney, 0);
            if (response == null || !response.isSuccess() || response.data() == null) {
                bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null,
                        response != null ? response.message() : "KhÃ´ng Ã¡p Ä‘Æ°á»£c voucher.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, response.data().voucherCode(),
                    "ÄÃ£ Ã¡p voucher " + response.data().voucherCode() + ".", null);
            SessionSupport.bindCurrentUser(model, session);
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            SessionSupport.bindCurrentUser(model, session);
        }
        return "sales";
    }

    @PostMapping("/sales/voucher/clear")
    public String clearVoucher(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String invoiceCode,
                               @RequestParam(required = false) String onlineOrderCode,
                               Model model,
                               HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        bindSalesPage(model, currentUser.id(), keyword, invoiceCode, onlineOrderCode, null, "ÄÃ£ gá»¡ voucher khá»i Ä‘Æ¡n táº¡i quáº§y.", null);
        SessionSupport.bindCurrentUser(model, session);
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
                           @RequestParam(required = false) String voucherCode,
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
                bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null, "Giá» bÃ¡n hÃ ng Ä‘ang trá»‘ng.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }

            List<InvoiceItemRequest> items = cart.items().stream()
                    .map(item -> new InvoiceItemRequest(item.productId(), item.quantity(), item.totalMoney()))
                    .toList();

            int subtotalMoney = cart.totalAmount() != null ? cart.totalAmount() : 0;
            int totalQuantity = cart.totalQuantity() != null ? cart.totalQuantity() : 0;
            VoucherValidationView appliedVoucher = null;
            String normalizedVoucherCode = normalizeVoucherCode(voucherCode);
            if (normalizedVoucherCode != null) {
                ApiResponse<VoucherValidationView> validation = voucherApiClient.validate(normalizedVoucherCode, subtotalMoney, 0);
                if (validation == null || !validation.isSuccess() || validation.data() == null) {
                    bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, null, null,
                            validation != null ? validation.message() : "Voucher khÃ´ng há»£p lá»‡.");
                    SessionSupport.bindCurrentUser(model, session);
                    return "sales";
                }
                appliedVoucher = validation.data();
            }

            int discountMoney = appliedVoucher != null && appliedVoucher.discountMoney() != null
                    ? appliedVoucher.discountMoney() : 0;
            int totalAmount = Math.max(subtotalMoney - discountMoney, 0);
            if (receiveMoney < totalAmount) {
                bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, normalizedVoucherCode, null,
                        "S? ti?n nh?n ch?a ?? ?? t?o h?a ??n.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }
            int moneyBack = Math.max(receiveMoney - totalAmount, 0);

            var response = invoiceApiClient.create(new CreateInvoiceRequest(
                    staffId,
                    phoneNumber,
                    fullName,
                    address,
                    totalQuantity,
                    subtotalMoney,
                    discountMoney,
                    totalAmount,
                    receiveMoney,
                    moneyBack,
                    appliedVoucher != null ? appliedVoucher.voucherCode() : null,
                    items
            ));

            InvoiceCreatedView invoice = response != null ? response.data() : null;
            if (response == null || !response.isSuccess() || invoice == null) {
                bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, normalizedVoucherCode, null,
                        response != null ? response.message() : "Ch?a t?o ???c h?a ??n.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }

            bindSalesPage(model, staffId, keyword, invoice.invoiceCode(), onlineOrderCode, null,
                    "ÄÃ£ táº¡o hÃ³a Ä‘Æ¡n " + invoice.invoiceCode() + ".", null);
            model.addAttribute("createdInvoice", invoice);
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, onlineOrderCode, voucherCode, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        }
    }

    @PostMapping("/sales/orders/pay")
    public String confirmOnlineOrderPayment(@RequestParam Long orderId,
                                            @RequestParam String orderCode,
                                            @RequestParam int receiveMoney,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String invoiceCode,
                                            @RequestParam(required = false) String voucherCode,
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
                bindSalesPage(model, staffId, keyword, invoiceCode, orderCode, voucherCode, null, "KhÃ´ng tÃ¬m tháº¥y Ä‘Æ¡n mua trÃªn website.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }
            if (receiveMoney < selectedOrder.totalMoney()) {
                bindSalesPage(model, staffId, keyword, invoiceCode, orderCode, voucherCode, null, "S? ti?n nh?n ch?a ?? ?? x?c nh?n thanh to?n.");
                SessionSupport.bindCurrentUser(model, session);
                return "sales";
            }

            ApiResponse<Void> response = orderApiClient.updateStatus(new UpdateOrderStatusRequest(
                    orderId,
                    "COMPLETED",
                    "PAID"
            ));

            bindSalesPage(model, staffId, keyword, invoiceCode, orderCode, voucherCode,
                    response != null && response.isSuccess()
                            ? "ÄÃ£ xÃ¡c nháº­n thanh toÃ¡n Ä‘Æ¡n " + orderCode + ". Tiá»n thá»«a: "
                            + (receiveMoney - selectedOrder.totalMoney()) + " VND"
                            : null,
                    response == null || response.isSuccess() ? null : response.message());
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (HttpStatusCodeException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, orderCode, voucherCode, null, extractApiError(ex));
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        } catch (RestClientException ex) {
            bindSalesPage(model, staffId, keyword, invoiceCode, orderCode, voucherCode, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            SessionSupport.bindCurrentUser(model, session);
            return "sales";
        }
    }

    private void bindSalesPage(Model model,
                               Long staffId,
                               String keyword,
                               String invoiceCode,
                               String onlineOrderCode,
                               String voucherCode,
                               String success,
                               String error) {
        model.addAttribute("staffId", staffId);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("invoiceCode", invoiceCode == null ? "" : invoiceCode);
        model.addAttribute("onlineOrderCode", onlineOrderCode == null ? "" : onlineOrderCode);
        model.addAttribute("voucherCode", voucherCode == null ? "" : voucherCode);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("activePage", "sales");
        model.addAttribute("productManagementUrl", "/products/manage");
        model.addAttribute("voucherManagementUrl", "/vouchers");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("customerLookupUrl", "/customers/lookup");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("shopUrl", "http://localhost:8083/products");

        try {
            model.addAttribute("products", productApiClient.getProducts(keyword));
            CartSummaryView cart = cartApiClient.getCart(staffId);
            model.addAttribute("cart", cart);
            VoucherValidationView appliedVoucher = resolveAppliedVoucher(cart, normalizeVoucherCode(voucherCode), model);
            int subtotalMoney = cart != null && cart.totalAmount() != null ? cart.totalAmount() : 0;
            int discountMoney = appliedVoucher != null && appliedVoucher.discountMoney() != null ? appliedVoucher.discountMoney() : 0;
            int finalTotalMoney = Math.max(subtotalMoney - discountMoney, 0);
            model.addAttribute("cartSubtotalMoney", subtotalMoney);
            model.addAttribute("cartDiscountMoney", discountMoney);
            model.addAttribute("cartFinalTotalMoney", finalTotalMoney);
            model.addAttribute("appliedVoucher", appliedVoucher);
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
            model.addAttribute("appliedVoucher", null);
            model.addAttribute("cartSubtotalMoney", 0);
            model.addAttribute("cartDiscountMoney", 0);
            model.addAttribute("cartFinalTotalMoney", 0);
            model.addAttribute("invoiceDetails", List.<InvoiceDetailView>of());
            model.addAttribute("onlineOrders", List.<OnlineOrderSummaryView>of());
            model.addAttribute("selectedOnlineOrder", null);
            model.addAttribute("onlineOrderDetails", List.<com.tdtu.cmsweb.api.dto.OrderDetailView>of());
            if (error == null) {
                model.addAttribute("error", "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            }
        }
    }

    private VoucherValidationView resolveAppliedVoucher(CartSummaryView cart, String voucherCode, Model model) {
        model.addAttribute("voucherCode", voucherCode == null ? "" : voucherCode);
        if (voucherCode == null || voucherCode.isBlank()) {
            return null;
        }
        if (cart == null || cart.items() == null || cart.items().isEmpty()) {
            model.addAttribute("voucherCode", "");
            return null;
        }
        ApiResponse<VoucherValidationView> response = voucherApiClient.validate(voucherCode, cart.totalAmount() == null ? 0 : cart.totalAmount(), 0);
        if (response == null || !response.isSuccess() || response.data() == null) {
            model.addAttribute("voucherCode", "");
            model.addAttribute("voucherWarning", response != null ? response.message() : "Voucher khÃ´ng cÃ²n há»£p lá»‡ cho giá» hiá»‡n táº¡i.");
            return null;
        }
        model.addAttribute("voucherCode", response.data().voucherCode());
        return response.data();
    }

    private String normalizeVoucherCode(String voucherCode) {
        if (voucherCode == null || voucherCode.isBlank()) {
            return null;
        }
        return voucherCode.trim().toUpperCase();
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
        return "Thong tin tao hoa don chua hop le.";
    }
}

