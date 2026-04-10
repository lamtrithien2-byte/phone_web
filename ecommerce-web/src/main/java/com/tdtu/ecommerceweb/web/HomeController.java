package com.tdtu.ecommerceweb.web;

import com.tdtu.ecommerceweb.api.ChatApiClient;
import com.tdtu.ecommerceweb.api.OrderApiClient;
import com.tdtu.ecommerceweb.api.ProductApiClient;
import com.tdtu.ecommerceweb.api.dto.ChatRequest;
import com.tdtu.ecommerceweb.api.dto.ChatResponse;
import com.tdtu.ecommerceweb.api.dto.CreateOrderItemRequest;
import com.tdtu.ecommerceweb.api.dto.CreateOrderRequest;
import com.tdtu.ecommerceweb.api.dto.OrderCreatedView;
import com.tdtu.ecommerceweb.api.dto.OrderDetailView;
import com.tdtu.ecommerceweb.api.dto.OrderSummaryView;
import com.tdtu.ecommerceweb.api.dto.ProductView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Controller
public class HomeController {

    private static final String PAGE_PRODUCTS = "products";
    private static final String PAGE_DETAIL = "detail";
    private static final String PAGE_CHECKOUT = "checkout";
    private static final int DEFAULT_QUANTITY = 1;

    private final ProductApiClient productApiClient;
    private final OrderApiClient orderApiClient;
    private final ChatApiClient chatApiClient;

    public HomeController(ProductApiClient productApiClient, OrderApiClient orderApiClient, ChatApiClient chatApiClient) {
        this.productApiClient = productApiClient;
        this.orderApiClient = orderApiClient;
        this.chatApiClient = chatApiClient;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String sort,
                           @RequestParam(required = false) Integer minPrice,
                           @RequestParam(required = false) Integer maxPrice,
                           @RequestParam(required = false) String chatSessionCode,
                           @RequestParam(required = false) String chatGuestName,
                           Model model) {
        renderProductsPage(model, sort, minPrice, maxPrice, chatSessionCode, chatGuestName, null, null);
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
                           Model model) {
        return renderCheckoutPage(model, productId, quantity, "", "", "", "", 0, null, null, null);
    }

    @PostMapping("/checkout/confirm")
    public String confirmCheckout(@RequestParam Long productId,
                                  @RequestParam(defaultValue = "1") int quantity,
                                  @RequestParam String fullName,
                                  @RequestParam String phoneNumber,
                                  @RequestParam String shippingAddress,
                                  @RequestParam(required = false) String note,
                                  @RequestParam(defaultValue = "0") Integer shippingFee,
                                  Model model) {
        ProductView product = findProduct(productId);
        if (product == null) {
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, shippingFee,
                    null, null, "Khong tim thay san pham");
        }
        if (quantity <= 0) {
            return renderCheckoutPage(model, productId, DEFAULT_QUANTITY, fullName, phoneNumber, shippingAddress, note, shippingFee,
                    null, null, "So luong khong hop le");
        }

        try {
            var response = orderApiClient.createOrder(new CreateOrderRequest(
                    phoneNumber,
                    fullName,
                    shippingAddress,
                    note,
                    shippingFee,
                    List.of(new CreateOrderItemRequest(product.id(), quantity, product.priceSale()))
            ));
            OrderCreatedView createdOrder = response != null ? response.data() : null;
            if (response == null || response.statusCode() != 200 || createdOrder == null) {
                return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, shippingFee,
                        null, null, response != null ? response.message() : "Khong tao duoc don hang");
            }
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, shippingFee,
                    createdOrder, "Dat hang thanh cong: " + createdOrder.orderCode(), null);
        } catch (RestClientException ex) {
            return renderCheckoutPage(model, productId, quantity, fullName, phoneNumber, shippingAddress, note, shippingFee,
                    null, null, "Core API is unavailable");
        }
    }

    @GetMapping("/tracking")
    public String tracking(@RequestParam(required = false) String phoneNumber,
                           @RequestParam(required = false) String orderCode,
                           Model model) {
        renderTrackingPage(model, phoneNumber, orderCode, null, null);
        return "tracking";
    }

    @PostMapping("/agent/chat")
    public String chat(@RequestParam String returnPage,
                       @RequestParam(required = false) Long productId,
                       @RequestParam(required = false) String chatMessage,
                       @RequestParam(required = false) String chatGuestName,
                       @RequestParam(required = false) String chatSessionCode,
                       Model model) {
        if (!StringUtils.hasText(chatMessage)) {
            return renderChatFallback(model, returnPage, productId, chatSessionCode, chatGuestName,
                    null, "Vui long nhap noi dung tu van");
        }

        try {
            ChatResponse chatResponse = chatApiClient.chat(new ChatRequest(
                    chatSessionCode,
                    null,
                    StringUtils.hasText(chatGuestName) ? chatGuestName : "Guest",
                    chatMessage
            ));
            String resolvedSession = chatResponse != null ? chatResponse.sessionCode() : chatSessionCode;
            return renderChatFallback(model, returnPage, productId, resolvedSession, chatGuestName, null, null);
        } catch (RestClientException ex) {
            return renderChatFallback(model, returnPage, productId, chatSessionCode, chatGuestName,
                    null, "Chat service is unavailable");
        }
    }

    private String renderChatFallback(Model model,
                                      String returnPage,
                                      Long productId,
                                      String chatSessionCode,
                                      String chatGuestName,
                                      String success,
                                      String error) {
        if (PAGE_DETAIL.equalsIgnoreCase(returnPage) && productId != null) {
            return renderProductDetailPage(model, productId, chatSessionCode, chatGuestName, success, error);
        }
        renderProductsPage(model, null, null, null, chatSessionCode, chatGuestName, success, error);
        return "products";
    }

    private void renderProductsPage(Model model,
                                    String sort,
                                    Integer minPrice,
                                    Integer maxPrice,
                                    String chatSessionCode,
                                    String chatGuestName,
                                    String success,
                                    String error) {
        List<ProductView> allProducts = loadProducts(null);
        List<ProductView> products = filterProducts(allProducts, sort, minPrice, maxPrice);
        model.addAttribute("pageTitle", "Danh sach san pham");
        model.addAttribute("selectedSort", defaultString(sort));
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
            renderProductsPage(model, null, null, null, chatSessionCode, chatGuestName, null, "Khong tim thay san pham");
            return "products";
        }

        model.addAttribute("pageTitle", product.name());
        model.addAttribute("product", product);
        model.addAttribute("quantity", DEFAULT_QUANTITY);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("relatedProducts", loadProducts(null).stream()
                .filter(item -> !Objects.equals(item.id(), product.id()))
                .sorted(Comparator.comparing(
                        item -> !Objects.equals(item.categoryName(), product.categoryName())
                ))
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
                                      Integer shippingFee,
                                      OrderCreatedView createdOrder,
                                      String success,
                                      String error) {
        ProductView product = findProduct(productId);
        if (product == null) {
            renderProductsPage(model, null, null, null, null, "", null, "Khong tim thay san pham");
            return "products";
        }

        int safeQuantity = Math.max(quantity, DEFAULT_QUANTITY);
        int fee = shippingFee != null ? Math.max(shippingFee, 0) : 0;
        int subtotal = safeQuantity * safePrice(product.priceSale());
        int grandTotal = subtotal + fee;

        model.addAttribute("pageTitle", "Thanh toan");
        model.addAttribute("product", product);
        model.addAttribute("quantity", safeQuantity);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("shippingFee", fee);
        model.addAttribute("fullName", defaultString(fullName));
        model.addAttribute("phoneNumber", defaultString(phoneNumber));
        model.addAttribute("shippingAddress", defaultString(shippingAddress));
        model.addAttribute("note", defaultString(note));
        model.addAttribute("createdOrder", createdOrder);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        return "checkout";
    }

    private void renderTrackingPage(Model model,
                                    String phoneNumber,
                                    String orderCode,
                                    String success,
                                    String error) {
        model.addAttribute("pageTitle", "Theo doi don hang");
        model.addAttribute("phoneNumber", defaultString(phoneNumber));
        model.addAttribute("orderCode", defaultString(orderCode));
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        try {
            model.addAttribute("orders", StringUtils.hasText(phoneNumber)
                    ? orderApiClient.getOrdersByPhone(phoneNumber)
                    : List.<OrderSummaryView>of());
            model.addAttribute("orderDetails", StringUtils.hasText(orderCode)
                    ? orderApiClient.getOrderDetail(orderCode)
                    : List.<OrderDetailView>of());
        } catch (RestClientException ex) {
            model.addAttribute("orders", List.<OrderSummaryView>of());
            model.addAttribute("orderDetails", List.<OrderDetailView>of());
            if (error == null) {
                model.addAttribute("error", "Core API is unavailable");
            }
        }
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
            case "newest" -> Comparator.comparing(ProductView::createdDate,
                    Comparator.nullsLast(Comparator.reverseOrder()));
            default -> Comparator.comparing(ProductView::updatedDate,
                    Comparator.nullsLast(Comparator.reverseOrder()));
        };

        return products.stream()
                .filter(product -> safePrice(product.priceSale()) >= safeMinPrice)
                .filter(product -> safePrice(product.priceSale()) <= safeMaxPrice)
                .sorted(comparator)
                .toList();
    }

    private int safePrice(Integer price) {
        return price != null ? price : 0;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
