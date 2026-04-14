package com.tdtu.cmsweb.api.dto;

public record AddCartItemRequest(Long staffId, Long productId, int quantity) {
}
