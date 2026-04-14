package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.dto.LoginResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

final class SessionSupport {

    static final String CURRENT_USER = "currentUser";

    private SessionSupport() {
    }

    static LoginResult getCurrentUser(HttpSession session) {
        Object value = session.getAttribute(CURRENT_USER);
        return value instanceof LoginResult loginResult ? loginResult : null;
    }

    static boolean requireLogin(HttpSession session, Model model) {
        if (getCurrentUser(session) == null) {
            model.addAttribute("error", "Please login first");
            return true;
        }
        return false;
    }

    static boolean requireAdmin(HttpSession session, Model model) {
        LoginResult user = getCurrentUser(session);
        if (user == null) {
            model.addAttribute("error", "Please login first");
            return true;
        }
        if (!"admin".equalsIgnoreCase(user.roleName())) {
            model.addAttribute("error", "Admin access only");
            return true;
        }
        return false;
    }

    static void bindCurrentUser(Model model, HttpSession session) {
        model.addAttribute("currentUser", getCurrentUser(session));
    }
}
