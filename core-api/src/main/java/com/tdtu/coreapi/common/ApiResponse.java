package com.tdtu.coreapi.common;

public record ApiResponse<T>(String status, int statusCode, String message, T data) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", 200, "Success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", 400, message, null);
    }
}
