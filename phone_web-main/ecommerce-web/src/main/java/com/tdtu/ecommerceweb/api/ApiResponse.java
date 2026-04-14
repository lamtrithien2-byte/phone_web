package com.tdtu.ecommerceweb.api;

public record ApiResponse<T>(int statusCode, String message, T data) {
}
