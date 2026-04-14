package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.ApiResponse;
import com.tdtu.cmsweb.api.AuthApiClient;
import com.tdtu.cmsweb.api.dto.ForgotPasswordResult;
import com.tdtu.cmsweb.api.dto.LoginRequest;
import com.tdtu.cmsweb.api.dto.LoginResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

@Controller
public class AuthController {

    private static final String SYSTEM_UNAVAILABLE_MESSAGE =
            "Hệ thống xử lý dữ liệu đang tạm thời không phản hồi.";

    private final AuthApiClient authApiClient;

    public AuthController(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(defaultValue = "admin") String role,
                            HttpSession session,
                            Model model) {
        if (SessionSupport.getCurrentUser(session) != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("selectedRole", normalizeRole(role));
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String role,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session
    ) {
        ApiResponse<LoginResult> response;
        String normalizedRole = normalizeRole(role);
        model.addAttribute("selectedRole", normalizedRole);
        model.addAttribute("username", username);
        try {
            response = authApiClient.login(new LoginRequest(username, password, normalizedRole));
        } catch (RestClientException ex) {
            model.addAttribute("error", SYSTEM_UNAVAILABLE_MESSAGE);
            return "login";
        }
        if (response == null || !response.isSuccess() || response.data() == null) {
            model.addAttribute("error", response != null ? response.message() : SYSTEM_UNAVAILABLE_MESSAGE);
            return "login";
        }
        session.setAttribute(SessionSupport.CURRENT_USER, response.data());
        if (Boolean.TRUE.equals(response.data().firstLogin())) {
            return "redirect:/change-password";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email, Model model) {
        try {
            ApiResponse<ForgotPasswordResult> response = authApiClient.forgotPassword(email);
            if (response == null || !response.isSuccess() || response.data() == null) {
                model.addAttribute("error", response != null ? response.message() : SYSTEM_UNAVAILABLE_MESSAGE);
                return "forgot-password";
            }
            model.addAttribute("success", response.data().message());
            model.addAttribute("deliveryMode", response.data().deliveryMode());
            model.addAttribute("previewFile", response.data().previewFile());
            return "forgot-password";
        } catch (RestClientException ex) {
            model.addAttribute("error", SYSTEM_UNAVAILABLE_MESSAGE);
            return "forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                Model model) {
        try {
            ApiResponse<Void> response = authApiClient.resetPassword(token, newPassword);
            if (response == null || !response.isSuccess()) {
                model.addAttribute("error", response != null ? response.message() : SYSTEM_UNAVAILABLE_MESSAGE);
                model.addAttribute("token", token);
                return "reset-password";
            }
            model.addAttribute("success", "Đặt lại mật khẩu thành công.");
            return "reset-password";
        } catch (RestClientException ex) {
            model.addAttribute("error", SYSTEM_UNAVAILABLE_MESSAGE);
            model.addAttribute("token", token);
            return "reset-password";
        }
    }

    @GetMapping("/change-password")
    public String changePasswordPage(HttpSession session, Model model) {
        if (SessionSupport.requireLogin(session, model)) {
            return "redirect:/login";
        }
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,
                                 Model model,
                                 HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            ApiResponse<Void> response = authApiClient.changePassword(currentUser.id(), newPassword);
            if (response == null || !response.isSuccess()) {
                model.addAttribute("error", response != null ? response.message() : SYSTEM_UNAVAILABLE_MESSAGE);
                return "change-password";
            }
            session.setAttribute(SessionSupport.CURRENT_USER, new LoginResult(
                    currentUser.id(),
                    currentUser.userName(),
                    currentUser.fullName(),
                    currentUser.email(),
                    currentUser.address(),
                    currentUser.phoneNumber(),
                    currentUser.avatar(),
                    currentUser.isActivated(),
                    false,
                    currentUser.isDeleted(),
                    currentUser.roleName()
            ));
            return "redirect:/dashboard";
        } catch (RestClientException ex) {
            model.addAttribute("error", SYSTEM_UNAVAILABLE_MESSAGE);
            return "change-password";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String normalizeRole(String role) {
        return "staff".equalsIgnoreCase(role) ? "staff" : "admin";
    }
}
