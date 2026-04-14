package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.UserApiClient;
import com.tdtu.cmsweb.api.dto.CreateStaffRequest;
import com.tdtu.cmsweb.api.dto.LoginResult;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

@Controller
public class EmployeeController {

    private final UserApiClient userApiClient;

    public EmployeeController(UserApiClient userApiClient) {
        this.userApiClient = userApiClient;
    }

    @GetMapping("/employees")
    public String employees(Model model, HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        bind(model, SessionSupport.getCurrentUser(session), null, null);
        return "employees";
    }

    @PostMapping("/employees")
    public String createEmployee(@RequestParam String userName,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 @RequestParam String email,
                                 @RequestParam String address,
                                 @RequestParam String phoneNumber,
                                 Model model,
                                 HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            userApiClient.createStaff(new CreateStaffRequest(userName, password, fullName, email, address, phoneNumber));
            bind(model, SessionSupport.getCurrentUser(session), "Đã tạo nhân viên mới.", null);
        } catch (RestClientException ex) {
            bind(model, SessionSupport.getCurrentUser(session), null, "Hệ thống xử lý dữ liệu đang tạm thời không phản hồi.");
        }
        return "employees";
    }

    @PostMapping("/employees/toggle")
    public String toggleEmployee(@RequestParam Long staffId,
                                 Model model,
                                 HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            userApiClient.toggleStatus(staffId);
            bind(model, SessionSupport.getCurrentUser(session), "Đã cập nhật trạng thái nhân viên.", null);
        } catch (RestClientException ex) {
            bind(model, SessionSupport.getCurrentUser(session), null, "Hệ thống xử lý dữ liệu đang tạm thời không phản hồi.");
        }
        return "employees";
    }

    private void bind(Model model, LoginResult currentUser, String success, String error) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        try {
            model.addAttribute("staffList", userApiClient.getStaffList());
        } catch (RestClientException ex) {
            model.addAttribute("staffList", java.util.List.of());
            model.addAttribute("error", error != null ? error : "Hệ thống xử lý dữ liệu đang tạm thời không phản hồi.");
        }
        model.addAttribute("activePage", "employees");
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("productManagementUrl", "/products/manage");
        model.addAttribute("voucherManagementUrl", "/vouchers");
        model.addAttribute("customerLookupUrl", "/customers/lookup");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("shopUrl", "http://localhost:8083/products");
    }
}
