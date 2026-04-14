package com.tdtu.ecommerceweb.web;

import com.tdtu.ecommerceweb.api.ChatApiClient;
import com.tdtu.ecommerceweb.api.InvoiceApiClient;
import com.tdtu.ecommerceweb.api.OrderApiClient;
import com.tdtu.ecommerceweb.api.ProductApiClient;
import com.tdtu.ecommerceweb.api.VoucherApiClient;
import com.tdtu.ecommerceweb.api.dto.ChatRequest;
import com.tdtu.ecommerceweb.api.dto.ChatResponse;
import com.tdtu.ecommerceweb.api.dto.CreateOrderItemRequest;
import com.tdtu.ecommerceweb.api.dto.CreateOrderRequest;
import com.tdtu.ecommerceweb.api.dto.CustomerView;
import com.tdtu.ecommerceweb.api.dto.InvoiceDetailView;
import com.tdtu.ecommerceweb.api.dto.OrderCreatedView;
import com.tdtu.ecommerceweb.api.dto.OrderDetailView;
import com.tdtu.ecommerceweb.api.dto.ProductView;
import com.tdtu.ecommerceweb.api.dto.TrackingRecordView;
import com.tdtu.ecommerceweb.api.dto.VoucherValidationView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Controller
public class    HomeController {

    private static final String PAGE_DETAIL = "detail";
    private static final int DEFAULT_QUANTITY = 1;

    private final ProductApiClient productApiClient;
    private final OrderApiClient orderApiClient;
    private final InvoiceApiClient invoiceApiClient;
    private final ChatApiClient chatApiClient;
    private final VoucherApiClient voucherApiClient;

    public HomeController(ProductApiClient productApiClient,
                          OrderApiClient orderApiClient,
                          InvoiceApiClient invoiceApiClient,
                          ChatApiClient chatApiClient,
                          VoucherApiClient voucherApiClient) {
        this.productApiClient = productApiClient;
        this.orderApiClient = orderApiClient;
        this.invoiceApiClient = invoiceApiClient;
        this.chatApiClient = chatApiClient;
        this.voucherApiClient = voucherApiClient;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String sort,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) Integer minPrice,
                           @RequestParam(required = false) Integer maxPrice,
                           @RequestParam(required = false) String chatSessionCode,
                           @RequestParam(required = false) String chatGuestName,
                           Model model) {
        renderProductsPage(model, sort, keyword, minPrice, maxPrice, chatSessionCode, chatGuestName, null, null);
        return "products";
    }

    @GetMapping("/products/{productId}")
    public String productDetail(@PathVariable Long productId,
                                @RequestParam(required = false) String chatSessionCode,
                                @RequestParam(required = false) String chatGuestName,
                                Model model) {
        return renderProductDetailPage(model, productId, chatSessionCode, chatGuestName, null, null);
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam Long productId,
                           @RequestParam(defaultValue = "1") int quantity,
                           @RequestParam(required = false) String chatSessionCode,
                           @RequestParam(required = false) String chatGuestName,
                           Model model) {
        return renderCheckoutPage(model, productId, quantity, "", "", "", "", "", 0,
                null, null, null, chatSessionCode, chatGuestName);
    }

    @PostMapping("/checkout/apply-voucher")
    public String applyVoucher(@RequestParam Long productId,
                               @RequestParam(defaultValue = "1") int quantity,
                               @RequestParam(required = false) String fullName,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String shippingAddress,
                               @RequestParam(required = false) String note,
                               @RequestParam(required = false) String voucherCode,
                               @RequestParam(defaultValue = "0") Integer shippingFee,
                               @RequestParam(required = false) String chatSessionCode,
                               @RequestParam(required = false) String chatGuestName,
                               Model model) {
        if (!StringUtils.hasText(voucherCode)) {
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, "",
                    shippingFee, null, null, "Vui lÃ²ng nháº­p mÃ£ voucher.", chatSessionCode, chatGuestName);
        }
        return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, voucherCode,
                shippingFee, null, "ÄÃ£ Ã¡p dá»¥ng voucher " + voucherCode.trim().toUpperCase() + ".", null, chatSessionCode, chatGuestName);
    }

    @PostMapping("/checkout/confirm")
    public String confirmCheckout(@RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") int quantity,
                                  @RequestParam String fullName,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String shippingAddress,
                                  @RequestParam(required = false) String note,
                                  @RequestParam(required = false) String voucherCode,
                                  @RequestParam(defaultValue = "0") Integer shippingFee,
                                  @RequestParam(required = false) String chatSessionCode,
                                  @RequestParam(required = false) String chatGuestName,
                                  Model model) {
        ProductView product = findProduct(productId);
        if (product == null) {
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, voucherCode, shippingFee,
                    null, null, "KhÃ´ng tÃ¬m tháº¥y sáº£n pháº©m.", chatSessionCode, chatGuestName);
        }
        if (quantity <= 0) {
            return renderCheckoutPage(model, productId, DEFAULT_QUANTITY, fullName, phoneNumber, shippingAddress, note, voucherCode, shippingFee,
                    null, null, "So luong chua hop le.", chatSessionCode, chatGuestName);
        }

        try {
            var response = orderApiClient.createOrder(new CreateOrderRequest(
                    phoneNumber,
                    fullName,
                    shippingAddress,
                    note,
                    voucherCode,
                    shippingFee,
                    List.of(new CreateOrderItemRequest(product.id(), quantity, product.priceSale()))
            ));
            OrderCreatedView createdOrder = response != null ? response.data() : null;
            if (response == null || response.statusCode() != 200 || createdOrder == null) {
                return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, voucherCode, shippingFee,
                        null, null, response != null ? response.message() : "Ch?a t?o ???c ??n mua.", chatSessionCode, chatGuestName);
            }
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, voucherCode, shippingFee,
                    createdOrder, "Äáº·t mua thÃ nh cÃ´ng. MÃ£ Ä‘Æ¡n cá»§a báº¡n lÃ  " + createdOrder.orderCode() + ".", null, chatSessionCode, chatGuestName);
        } catch (RestClientException ex) {
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, voucherCode, shippingFee,
                    null, null, "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.", chatSessionCode, chatGuestName);
        }
    }

    @GetMapping("/tracking")
    public String tracking(@RequestParam(required = false) String phoneNumber,
                           @RequestParam(required = false) String orderCode,
                           @RequestParam(required = false) String chatSessionCode,
                           @RequestParam(required = false) String chatGuestName,
                           Model model) {
        renderTrackingPage(model, phoneNumber, orderCode, null, null, chatSessionCode, chatGuestName);
        return "tracking";
    }

    @PostMapping("/agent/chat")
    public String chat(@RequestParam String returnPage,
                       @RequestParam(required = false) Long productId,
                       @RequestParam(required = false) Integer quantity,
                       @RequestParam(required = false) String fullName,
                       @RequestParam(required = false) String phoneNumber,
                       @RequestParam(required = false) String shippingAddress,
                       @RequestParam(required = false) String note,
                       @RequestParam(required = false) String voucherCode,
                       @RequestParam(required = false) Integer shippingFee,
                       @RequestParam(required = false) String orderCode,
                       @RequestParam(required = false) String chatMessage,
                       @RequestParam(required = false) String chatGuestName,
                       @RequestParam(required = false) String chatSessionCode,
                       Model model) {
        if (!StringUtils.hasText(chatMessage)) {
            return renderChatFallback(model, returnPage, productId, quantity, fullName, phoneNumber, shippingAddress, note,
                    voucherCode, shippingFee, orderCode, chatSessionCode, chatGuestName, null, "Vui lÃ²ng nháº­p ná»™i dung cáº§n há»— trá»£.");
        }

        try {
            ChatResponse chatResponse = chatApiClient.chat(new ChatRequest(
                    chatSessionCode,
                    null,
                    StringUtils.hasText(chatGuestName) ? chatGuestName : "Guest",
                    chatMessage
            ));
            String resolvedSession = chatResponse != null ? chatResponse.sessionCode() : chatSessionCode;
            return renderChatFallback(model, returnPage, productId, quantity, fullName, phoneNumber, shippingAddress, note,
                    voucherCode, shippingFee, orderCode, resolvedSession, chatGuestName, null, null);
        } catch (RestClientException ex) {
            return renderChatFallback(model, returnPage, productId, quantity, fullName, phoneNumber, shippingAddress, note,
                    voucherCode, shippingFee, orderCode, chatSessionCode, chatGuestName, null, "KÃªnh há»— trá»£ Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
        }
    }

    private String renderChatFallback(Model model,
                                      String returnPage,
                                      Long productId,
                                      Integer quantity,
                                      String fullName,
                                      String phoneNumber,
                                      String shippingAddress,
                                      String note,
                                      String voucherCode,
                                      Integer shippingFee,
                                      String orderCode,
                                      String chatSessionCode,
                                      String chatGuestName,
                                      String success,
                                      String error) {
        if (PAGE_DETAIL.equalsIgnoreCase(returnPage) && productId != null) {
            return renderProductDetailPage(model, productId, chatSessionCode, chatGuestName, success, error);
        }
        if ("checkout".equalsIgnoreCase(returnPage) && productId != null) {
            return renderCheckoutPage(model, productId, quantity != null ? quantity : DEFAULT_QUANTITY,
                    defaultString(fullName), defaultString(phoneNumber), defaultString(shippingAddress), defaultString(note),
                    defaultString(voucherCode), shippingFee != null ? shippingFee : 0, null, success, error, chatSessionCode, chatGuestName);
        }
        if ("tracking".equalsIgnoreCase(returnPage)) {
            renderTrackingPage(model, phoneNumber, orderCode, success, error, chatSessionCode, chatGuestName);
            return "tracking";
        }
        renderProductsPage(model, null, null, null, null, chatSessionCode, chatGuestName, success, error);
        return "products";
    }

    private void renderProductsPage(Model model,
                                    String sort,
                                    String keyword,
                                    Integer minPrice,
                                    Integer maxPrice,
                                    String chatSessionCode,
                                    String chatGuestName,
                                    String success,
                                    String error) {
        List<ProductView> allProducts = loadProducts(keyword);
        List<ProductView> products = filterProducts(allProducts, sort, minPrice, maxPrice);
        model.addAttribute("pageTitle", "Danh s?ch s?n ph?m");
        model.addAttribute("selectedSort", defaultString(sort));
        model.addAttribute("keyword", defaultString(keyword));
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("products", products);
        model.addAttribute("featuredProducts", allProducts.stream().limit(4).toList());
        bindChat(model, chatSessionCode, chatGuestName);
    }

    private String renderProductDetailPage(Model model,
                                           Long productId,
                                           String chatSessionCode,
                                           String chatGuestName,
                                           String success,
                                           String error) {
        ProductView product = findProduct(productId);
        if (product == null) {
            renderProductsPage(model, null, null, null, null, chatSessionCode, chatGuestName, null, "KhÃ´ng tÃ¬m tháº¥y sáº£n pháº©m.");
            return "products";
        }

        model.addAttribute("pageTitle", product.name());
        model.addAttribute("product", product);
        model.addAttribute("quantity", DEFAULT_QUANTITY);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("relatedProducts", loadProducts(null).stream()
                .filter(item -> !Objects.equals(item.id(), product.id()))
                .filter(item -> Objects.equals(item.categoryName(), product.categoryName()))
                .sorted(Comparator.comparing(ProductView::updatedDate, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(4)
                .toList());
        bindChat(model, chatSessionCode, chatGuestName);
        return "product-detail";
    }

    private String renderCheckoutPage(Model model,
                                      Long productId,
                                      int quantity,
                                      String fullName,
                                      String phoneNumber,
                                      String shippingAddress,
                                      String note,
                                      String voucherCode,
                                      Integer shippingFee,
                                      OrderCreatedView createdOrder,
                                      String success,
                                      String error,
                                      String chatSessionCode,
                                      String chatGuestName) {
        ProductView product = findProduct(productId);
        if (product == null) {
            renderProductsPage(model, null, null, null, null, null, "", null, "KhÃ´ng tÃ¬m tháº¥y sáº£n pháº©m.");
            return "products";
        }

        int safeQuantity = Math.max(quantity, DEFAULT_QUANTITY);
        int fee = shippingFee != null ? Math.max(shippingFee, 0) : 0;
        PricingSummary pricing = resolvePricing(product, safeQuantity, fee, voucherCode);

        model.addAttribute("pageTitle", "Thanh toan");
        model.addAttribute("product", product);
        model.addAttribute("quantity", safeQuantity);
        model.addAttribute("subtotal", pricing.subtotal());
        model.addAttribute("totalBeforeDiscount", pricing.totalBeforeDiscount());
        model.addAttribute("discountMoney", pricing.discountMoney());
        model.addAttribute("grandTotal", pricing.totalAfterDiscount());
        model.addAttribute("shippingFee", fee);
        model.addAttribute("voucherCode", pricing.voucherCode());
        model.addAttribute("appliedVoucher", pricing.voucher());
        model.addAttribute("fullName", defaultString(fullName));
        model.addAttribute("phoneNumber", defaultString(phoneNumber));
        model.addAttribute("shippingAddress", defaultString(shippingAddress));
        model.addAttribute("note", defaultString(note));
        model.addAttribute("createdOrder", createdOrder);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        bindChat(model, chatSessionCode, chatGuestName);
        return "checkout";
    }

    private void renderTrackingPage(Model model,
                                    String phoneNumber,
                                    String orderCode,
                                    String success,
                                    String error,
                                    String chatSessionCode,
                                    String chatGuestName) {
        model.addAttribute("pageTitle", "Tra cuu don hang");
        model.addAttribute("phoneNumber", defaultString(phoneNumber));
        model.addAttribute("orderCode", defaultString(orderCode));
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        try {
            List<TrackingRecordView> orders = StringUtils.hasText(phoneNumber) ? loadTrackingRecords(phoneNumber) : List.of();
            String selectedCode = StringUtils.hasText(orderCode)
                    ? orderCode
                    : (!orders.isEmpty() ? orders.get(0).code() : "");
            TrackingRecordView selectedRecord = orders.stream()
                    .filter(item -> item.code().equalsIgnoreCase(selectedCode))
                    .findFirst()
                    .orElse(null);

            model.addAttribute("orders", orders);
            model.addAttribute("selectedRecord", selectedRecord);
            model.addAttribute("orderDetails",
                    StringUtils.hasText(selectedCode) && selectedCode.regionMatches(true, 0, "ORD-", 0, 4)
                            ? orderApiClient.getOrderDetail(selectedCode)
                            : List.<OrderDetailView>of());
            model.addAttribute("invoiceDetails",
                    StringUtils.hasText(selectedCode) && selectedCode.regionMatches(true, 0, "INV-", 0, 4)
                            ? invoiceApiClient.getInvoiceDetail(selectedCode)
                            : List.<InvoiceDetailView>of());
        } catch (RestClientException ex) {
            model.addAttribute("orders", List.<TrackingRecordView>of());
            model.addAttribute("selectedRecord", null);
            model.addAttribute("orderDetails", List.<OrderDetailView>of());
            model.addAttribute("invoiceDetails", List.<InvoiceDetailView>of());
            if (error == null) {
                model.addAttribute("error", "Há»‡ thá»‘ng xá»­ lÃ½ dá»¯ liá»‡u Ä‘ang táº¡m thá»i khÃ´ng pháº£n há»“i.");
            }
        }
        bindChat(model, chatSessionCode, chatGuestName);
    }

    private List<TrackingRecordView> loadTrackingRecords(String phoneNumber) {
        List<TrackingRecordView> onlineOrders = orderApiClient.getOrdersByPhone(phoneNumber).stream()
                .map(order -> new TrackingRecordView(
                        order.orderCode(),
                        order.customerName(),
                        "Online",
                        order.orderStatus(),
                        order.paymentStatus(),
                        null,
                        order.totalMoney(),
                        order.createdAt()
                ))
                .toList();

        CustomerView customer = invoiceApiClient.getCustomerByPhone(phoneNumber);
        List<TrackingRecordView> invoices = customer != null
                ? invoiceApiClient.getHistory(customer.id()).stream()
                .map(history -> new TrackingRecordView(
                        history.invoiceCode(),
                        history.customerName(),
                        "Tai quay",
                        "COMPLETED",
                        "PAID",
                        history.salesStaffName(),
                        history.totalMoney(),
                        history.createdDate()
                ))
                .toList()
                : List.of();

        return Stream.concat(onlineOrders.stream(), invoices.stream())
                .sorted(Comparator.comparing(TrackingRecordView::createdAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(java.util.stream.Collectors.toMap(
                        item -> defaultString(item.code()).trim().toLowerCase(),
                        item -> item,
                        (first, second) -> first,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();
    }

    private void bindChat(Model model, String chatSessionCode, String chatGuestName) {
        model.addAttribute("chatSessionCode", defaultString(chatSessionCode));
        model.addAttribute("chatGuestName", defaultString(chatGuestName));
        model.addAttribute("chatOpen", StringUtils.hasText(chatSessionCode));
        try {
            model.addAttribute("chatHistory", StringUtils.hasText(chatSessionCode)
                    ? emptyIfNull(chatApiClient.getHistory(chatSessionCode))
                    : null);
        } catch (RestClientException ex) {
            model.addAttribute("chatHistory", null);
        }
    }

    private List<ProductView> loadProducts(String keyword) {
        try {
            return productApiClient.findProducts(keyword);
        } catch (RestClientException ex) {
            return List.of();
        }
    }

    private ProductView findProduct(Long productId) {
        if (productId == null) {
            return null;
        }
        return loadProducts(null).stream()
                .filter(product -> Objects.equals(product.id(), productId))
                .findFirst()
                .orElse(null);
    }

    private ChatResponse emptyIfNull(ChatResponse response) {
        return response != null ? response : new ChatResponse(null, null, List.of());
    }

    private List<ProductView> filterProducts(List<ProductView> products,
                                             String sort,
                                             Integer minPrice,
                                             Integer maxPrice) {
        String normalizedSort = defaultString(sort).trim().toLowerCase();
        int safeMinPrice = minPrice != null ? Math.max(minPrice, 0) : Integer.MIN_VALUE;
        int safeMaxPrice = maxPrice != null ? Math.max(maxPrice, 0) : Integer.MAX_VALUE;

        Comparator<ProductView> comparator = switch (normalizedSort) {
            case "price_asc" -> Comparator.comparingInt(product -> safePrice(product.priceSale()));
            case "price_desc" -> Comparator.comparingInt((ProductView product) -> safePrice(product.priceSale())).reversed();
            case "best_selling" -> Comparator.comparingInt((ProductView product) -> product.saleNumber() != null ? product.saleNumber() : 0).reversed();
            case "newest" -> Comparator.comparing(ProductView::createdDate, Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(ProductView::updatedDate, Comparator.nullsLast(Comparator.reverseOrder()));
        };

        return products.stream()
                .filter(product -> safePrice(product.priceSale()) >= safeMinPrice)
                .filter(product -> safePrice(product.priceSale()) <= safeMaxPrice)
                .sorted(comparator)
                .toList();
    }

    private PricingSummary resolvePricing(ProductView product, int quantity, int shippingFee, String voucherCode) {
        int subtotal = quantity * safePrice(product.priceSale());
        int totalBeforeDiscount = subtotal + shippingFee;
        if (!StringUtils.hasText(voucherCode)) {
            return new PricingSummary(subtotal, totalBeforeDiscount, 0, totalBeforeDiscount, "", null);
        }

        try {
            var response = voucherApiClient.validate(voucherCode.trim().toUpperCase(), subtotal, shippingFee);
            VoucherValidationView voucher = response != null ? response.data() : null;
            if (response == null || response.statusCode() != 200 || voucher == null) {
                return new PricingSummary(subtotal, totalBeforeDiscount, 0, totalBeforeDiscount, voucherCode.trim().toUpperCase(), null);
            }
            return new PricingSummary(
                    subtotal,
                    voucher.totalBeforeDiscount(),
                    voucher.discountMoney(),
                    voucher.totalAfterDiscount(),
                    voucher.voucherCode(),
                    voucher
            );
        } catch (RestClientException ex) {
            return new PricingSummary(subtotal, totalBeforeDiscount, 0, totalBeforeDiscount, voucherCode.trim().toUpperCase(), null);
        }
    }

    private int safePrice(Integer price) {
        return price != null ? price : 0;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private record PricingSummary(
            int subtotal,
            int totalBeforeDiscount,
            int discountMoney,
            int totalAfterDiscount,
            String voucherCode,
            VoucherValidationView voucher
    ) {
    }
}

