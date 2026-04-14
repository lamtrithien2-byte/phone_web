package com.tdtu.cmsweb.api.dto;

public record ForgotPasswordResult(
        String deliveryMode,
        String previewFile,
        String message
) {
}
