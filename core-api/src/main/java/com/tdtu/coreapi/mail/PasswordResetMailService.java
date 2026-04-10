package com.tdtu.coreapi.mail;

import com.tdtu.coreapi.auth.dto.ForgotPasswordResult;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PasswordResetMailService {

    private final MailSettings mailSettings;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    public PasswordResetMailService(MailSettings mailSettings, ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSettings = mailSettings;
        this.mailSenderProvider = mailSenderProvider;
    }

    public ForgotPasswordResult sendResetPasswordMail(String email, String token) {
        String resetUrl = String.format(mailSettings.cmsResetUrlTemplate(), token);
        if ("smtp".equalsIgnoreCase(mailSettings.deliveryMode()) && mailSenderProvider.getIfAvailable() != null) {
            sendViaSmtp(email, resetUrl);
            return new ForgotPasswordResult(
                    "smtp",
                    null,
                    "Reset password email has been sent"
            );
        }
        String previewFile = writePreview(email, resetUrl);
        return new ForgotPasswordResult(
                "preview",
                previewFile,
                "SMTP is not configured. A local mail preview has been created"
        );
    }

    private void sendViaSmtp(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailSettings.fromAddress());
        message.setTo(email);
        message.setSubject("Phone Store - Password Reset");
        message.setText("""
                Xin chao,

                Ban vua yeu cau dat lai mat khau cho tai khoan Phone Store.
                Vui long mo lien ket sau de dat lai mat khau:
                %s

                Neu ban khong thuc hien yeu cau nay, hay bo qua email.
                """.formatted(resetUrl));
        mailSenderProvider.getObject().send(message);
    }

    private String writePreview(String email, String resetUrl) {
        try {
            Path previewDir = Path.of(mailSettings.previewDir());
            Files.createDirectories(previewDir);
            String fileName = "reset-mail-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt";
            Path previewFile = previewDir.resolve(fileName);
            String content = """
                    To: %s
                    From: %s
                    Subject: Phone Store - Password Reset

                    Xin chao,

                    Ban vua yeu cau dat lai mat khau cho tai khoan Phone Store.
                    Vui long mo lien ket sau de dat lai mat khau:
                    %s

                    Neu ban khong thuc hien yeu cau nay, hay bo qua email.
                    """.formatted(email, mailSettings.fromAddress(), resetUrl);
            Files.writeString(previewFile, content);
            return previewFile.toAbsolutePath().toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write local mail preview", ex);
        }
    }
}
