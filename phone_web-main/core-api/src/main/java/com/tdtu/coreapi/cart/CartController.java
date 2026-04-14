package com.tdtu.coreapi.cart;

import com.tdtu.coreapi.cart.dto.AddCartItemRequest;
import com.tdtu.coreapi.cart.dto.CartSummaryView;
import com.tdtu.coreapi.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
public class  CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartSummaryView> getCart(@RequestParam Long staffId) {
        return ApiResponse.success(cartService.getCart(staffId));
    }

    @PostMapping("/items")
    public ApiResponse<CartSummaryView> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ApiResponse.success(cartService.addItem(request));
    }

    @DeleteMapping
    public ApiResponse<String> clearCart(@RequestParam Long staffId) {
        cartService.clearCart(staffId);
        return ApiResponse.success("Cart cleared");
    }

    @DeleteMapping("/items/{cartItemId}")
    public ApiResponse<CartSummaryView> deleteItem(@PathVariable Long cartItemId,
                                                   @RequestParam Long staffId) {
        return ApiResponse.success(cartService.deleteItem(staffId, cartItemId));
    }
}
