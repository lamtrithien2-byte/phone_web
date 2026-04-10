package com.tdtu.coreapi.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
public record MailSettings(
        String deliveryMode,
        String fromAddress,
        String fromName,
        String previewDir,
        String cmsResetUrlTemplate
) {
}
