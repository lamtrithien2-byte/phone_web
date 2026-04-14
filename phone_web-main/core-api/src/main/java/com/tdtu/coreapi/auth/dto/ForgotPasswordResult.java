package com.tdtu.coreapi.auth.dto;

public record ForgotPasswordResult(
        String deliveryMode,
        String previewFile,
        String message
) {
}
