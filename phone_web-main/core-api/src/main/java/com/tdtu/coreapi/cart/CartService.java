package com.tdtu.coreapi.cart;

import com.tdtu.coreapi.cart.dto.AddCartItemRequest;
import com.tdtu.coreapi.cart.dto.CartItemView;
import com.tdtu.coreapi.cart.dto.CartSummaryView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private final CartProcedureRepository cartProcedureRepository;

    public CartService(CartProcedureRepository cartProcedureRepository) {
        this.cartProcedureRepository = cartProcedureRepository;
    }

    public CartSummaryView getCart(Long staffId) {
        List<CartItemView> items = cartProcedureRepository.getCartByStaffId(staffId);
        int totalQuantity = items.stream().mapToInt(CartItemView::quantity).sum();
        int totalAmount = items.stream().mapToInt(CartItemView::totalMoney).sum();
        return new CartSummaryView(totalQuantity, totalAmount, items);
    }

    public CartSummaryView addItem(AddCartItemRequest request) {
        cartProcedureRepository.addItem(request.staffId(), request.productId(), request.quantity());
        return getCart(request.staffId());
    }

    public void clearCart(Long staffId) {
        cartProcedureRepository.clearByStaffId(staffId);
    }

    public CartSummaryView deleteItem(Long staffId, Long cartItemId) {
        cartProcedureRepository.deleteItem(cartItemId);
        return getCart(staffId);
    }
}
