package com.tdtu.coreapi.auth;

import com.tdtu.coreapi.auth.dto.LoginRequest;
import com.tdtu.coreapi.auth.dto.ForgotPasswordResult;
import com.tdtu.coreapi.auth.dto.LoginUserView;
import com.tdtu.coreapi.mail.PasswordResetMailService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthProcedureRepository authProcedureRepository;
    private final PasswordResetMailService passwordResetMailService;

    public AuthService(AuthProcedureRepository authProcedureRepository,
                       PasswordResetMailService passwordResetMailService) {
        this.authProcedureRepository = authProcedureRepository;
        this.passwordResetMailService = passwordResetMailService;
    }

    public LoginUserView login(LoginRequest request) {
        LoginUserView user = authProcedureRepository.loginByUserName(request.username());
        if (user == null) {
            return null;
        }

        String requestedRole = normalizeRole(request.role());
        String actualRole = normalizeRole(user.roleName());
        if (requestedRole != null && actualRole != null && !requestedRole.equals(actualRole)) {
            throw new IllegalArgumentException("Account does not belong to selected role");
        }

        if (!Boolean.TRUE.equals(user.isActivated())) {
            throw new IllegalArgumentException("Account is not activated");
        }

        if (!matchesPassword(request.password(), user.password())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return user;
    }

    public void changeFirstPassword(Long userId, String newPassword) {
        String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        authProcedureRepository.changeFirstPassword(userId, passwordHash);
    }

    public ForgotPasswordResult forgotPassword(String email) {
        String token = java.util.UUID.randomUUID().toString();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.HOUR, 24);
        authProcedureRepository.createResetToken(email, token, cal.getTime());
        return passwordResetMailService.sendResetPasswordMail(email, token);
    }

    public void resetPassword(String token, String newPassword) {
        String passwordHash = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        authProcedureRepository.resetPassword(token, passwordHash);
    }

    public void activateAccount(String token) {
        authProcedureRepository.activateAccount(token);
    }

    private boolean matchesPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$") || storedPassword.startsWith("$2y$")) {
            return BCrypt.checkpw(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        return switch (role.trim().toLowerCase()) {
            case "administrator" -> "admin";
            case "employee" -> "staff";
            default -> role.trim().toLowerCase();
        };
    }
}
