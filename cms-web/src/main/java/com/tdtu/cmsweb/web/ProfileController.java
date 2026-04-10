package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.UserApiClient;
import com.tdtu.cmsweb.api.dto.LoginResult;
import com.tdtu.cmsweb.api.dto.UpdateProfileRequest;
import com.tdtu.cmsweb.api.dto.UserView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

@Controller
public class ProfileController {

    private final UserApiClient userApiClient;

    public ProfileController(UserApiClient userApiClient) {
        this.userApiClient = userApiClient;
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (SessionSupport.requireLogin(session, model)) {
            return "redirect:/login";
        }
        bind(model, SessionSupport.getCurrentUser(session), null, null);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String email,
                                @RequestParam String address,
                                @RequestParam String phoneNumber,
                                @RequestParam(required = false) String avatar,
                                Model model,
                                HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            UserView updatedUser = userApiClient.updateProfile(new UpdateProfileRequest(currentUser.id(), fullName, email, address, phoneNumber, avatar));
            if (updatedUser != null) {
                session.setAttribute(SessionSupport.CURRENT_USER, new LoginResult(
                        updatedUser.id(),
                        updatedUser.userName(),
                        updatedUser.fullName(),
                        updatedUser.email(),
                        updatedUser.address(),
                        updatedUser.phoneNumber(),
                        updatedUser.avatar(),
                        updatedUser.isActivated(),
                        updatedUser.firstLogin(),
                        updatedUser.isDeleted(),
                        updatedUser.roleName()
                ));
            }
            bind(model, SessionSupport.getCurrentUser(session), "Da cap nhat ho so", null);
        } catch (RestClientException ex) {
            bind(model, currentUser, null, "Core API is unavailable");
        }
        return "profile";
    }

    private void bind(Model model, LoginResult currentUser, String success, String error) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("isAdmin", currentUser != null && "admin".equalsIgnoreCase(currentUser.roleName()));
        try {
            UserView user = userApiClient.getProfile(currentUser.id());
            model.addAttribute("user", user);
        } catch (RestClientException ex) {
            model.addAttribute("user", null);
            model.addAttribute("error", error != null ? error : "Core API is unavailable");
        }
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("shopUrl", "http://127.0.0.1:8083/products");
    }
}
