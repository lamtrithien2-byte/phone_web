package com.tdtu.cmsweb.api;

public record ApiResponse<T>(String status, int statusCode, String message, T data) {
    public boolean isSuccess() {
        return (statusCode >= 200 && statusCode < 300) || "success".equalsIgnoreCase(status);
    }
}
