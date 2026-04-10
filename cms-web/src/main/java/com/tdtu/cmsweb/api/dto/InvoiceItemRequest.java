package com.tdtu.cmsweb.api.dto;

public record InvoiceItemRequest(Long productId, int quantity, int totalMoney) {
}
