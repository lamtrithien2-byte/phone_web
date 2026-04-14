package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.ApiResponse;
import com.tdtu.cmsweb.api.VoucherApiClient;
import com.tdtu.cmsweb.api.dto.LoginResult;
import com.tdtu.cmsweb.api.dto.VoucherUpsertRequest;
import com.tdtu.cmsweb.api.dto.VoucherView;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class VoucherController {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final VoucherApiClient voucherApiClient;

    public VoucherController(VoucherApiClient voucherApiClient) {
        this.voucherApiClient = voucherApiClient;
    }

    @GetMapping("/vouchers")
    public String vouchers(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) String voucherType,
                           @RequestParam(required = false) Long editId,
                           @RequestParam(required = false) Boolean generateCode,
                           @RequestParam(required = false) String success,
                           Model model,
                           HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }

        List<VoucherView> vouchers = voucherApiClient.getAll(keyword, status, voucherType);
        VoucherView editingVoucher = editId != null ? voucherApiClient.getById(editId) : null;
        String generatedCode = Boolean.TRUE.equals(generateCode) ? voucherApiClient.generateCode() : null;
        bindPage(model, session, vouchers, editingVoucher, keyword, status, voucherType, success, null, generatedCode);
        return "vouchers";
    }

    @PostMapping("/vouchers/create")
    public String create(@RequestParam String voucherCode,
                         @RequestParam String voucherName,
                         @RequestParam String voucherType,
                         @RequestParam Integer discountValue,
                         @RequestParam(defaultValue = "false") boolean active,
                         @RequestParam(required = false) String startsAt,
                         @RequestParam(required = false) String endsAt,
                         @RequestParam(defaultValue = "0") Integer maxUsage,
                         @RequestParam(defaultValue = "0") Integer minOrderValue,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String filterVoucherType,
                         Model model,
                         HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        VoucherUpsertRequest request = new VoucherUpsertRequest(
                voucherCode, voucherName, voucherType, discountValue, active,
                parseDateTime(startsAt), parseDateTime(endsAt), maxUsage, minOrderValue
        );
        ApiResponse<Long> response = voucherApiClient.create(request);
        if (response != null && response.isSuccess()) {
            return "redirect:/vouchers?success=??+t?o+voucher";
        }
        bindCreateFallback(model, session, keyword, status, filterVoucherType, request,
                response != null ? response.message() : "Kh?ng t?o ???c voucher.");
        return "vouchers";
    }

    @PostMapping("/vouchers/update")
    public String update(@RequestParam Long voucherId,
                         @RequestParam String voucherCode,
                         @RequestParam String voucherName,
                         @RequestParam String voucherType,
                         @RequestParam Integer discountValue,
                         @RequestParam(defaultValue = "false") boolean active,
                         @RequestParam(required = false) String startsAt,
                         @RequestParam(required = false) String endsAt,
                         @RequestParam(defaultValue = "0") Integer maxUsage,
                         @RequestParam(defaultValue = "0") Integer minOrderValue,
                         @RequestParam(required = false) String keyword,
                         @RequestParam(required = false) String status,
                         @RequestParam(required = false) String filterVoucherType,
                         Model model,
                         HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        VoucherUpsertRequest request = new VoucherUpsertRequest(
                voucherCode, voucherName, voucherType, discountValue, active,
                parseDateTime(startsAt), parseDateTime(endsAt), maxUsage, minOrderValue
        );
        ApiResponse<Void> response = voucherApiClient.update(voucherId, request);
        if (response != null && response.isSuccess()) {
            return "redirect:/vouchers?success=??+c?p+nh?t+voucher";
        }
        bindCreateFallback(model, session, keyword, status, filterVoucherType, request,
                response != null ? response.message() : "Kh?ng c?p nh?t ???c voucher.");
        model.addAttribute("editingVoucherId", voucherId);
        return "vouchers";
    }

    @PostMapping("/vouchers/toggle")
    public String toggle(@RequestParam Long voucherId,
                         @RequestParam boolean active) {
        voucherApiClient.toggle(voucherId, active);
        return "redirect:/vouchers?success=??+c?p+nh?t+tr?ng+th?i+voucher";
    }

    @PostMapping("/vouchers/delete")
    public String delete(@RequestParam Long voucherId) {
        voucherApiClient.delete(voucherId);
        return "redirect:/vouchers?success=??+x?a+voucher";
    }

    private void bindCreateFallback(Model model,
                                    HttpSession session,
                                    String keyword,
                                    String status,
                                    String voucherType,
                                    VoucherUpsertRequest request,
                                    String error) {
        List<VoucherView> vouchers = voucherApiClient.getAll(keyword, status, voucherType);
        bindPage(model, session, vouchers, null, keyword, status, voucherType, null, error, null);
        model.addAttribute("formVoucherCode", request.voucherCode());
        model.addAttribute("formVoucherName", request.voucherName());
        model.addAttribute("formVoucherType", request.voucherType());
        model.addAttribute("formDiscountValue", request.discountValue());
        model.addAttribute("formActive", request.active());
        model.addAttribute("formStartsAt", formatDateTime(request.startsAt()));
        model.addAttribute("formEndsAt", formatDateTime(request.endsAt()));
        model.addAttribute("formMaxUsage", request.maxUsage());
        model.addAttribute("formMinOrderValue", request.minOrderValue());
    }

    private void bindPage(Model model,
                          HttpSession session,
                          List<VoucherView> vouchers,
                          VoucherView editingVoucher,
                          String keyword,
                          String status,
                          String voucherType,
                          String success,
                          String error,
                          String generatedCode) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        model.addAttribute("role", currentUser != null ? currentUser.roleName() : "");
        SessionSupport.bindCurrentUser(model, session);
        model.addAttribute("activePage", "vouchers");
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("productManagementUrl", "/products/manage");
        model.addAttribute("voucherManagementUrl", "/vouchers");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("customerLookupUrl", "/customers/lookup");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("shopUrl", "http://localhost:8083/products");
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("statusFilter", status == null ? "" : status);
        model.addAttribute("voucherTypeFilter", voucherType == null ? "" : voucherType);
        model.addAttribute("success", success);
        model.addAttribute("error", error);
        model.addAttribute("editingVoucherId", editingVoucher != null ? editingVoucher.id() : null);
        model.addAttribute("formVoucherCode", editingVoucher != null ? editingVoucher.voucherCode() : generatedCode == null ? "" : generatedCode);
        model.addAttribute("formVoucherName", editingVoucher != null ? editingVoucher.voucherName() : "");
        model.addAttribute("formVoucherType", editingVoucher != null ? editingVoucher.voucherType() : "PERCENT");
        model.addAttribute("formDiscountValue", editingVoucher != null ? editingVoucher.discountValue() : 0);
        model.addAttribute("formActive", editingVoucher != null ? editingVoucher.active() : true);
        model.addAttribute("formStartsAt", editingVoucher != null ? formatDateTime(editingVoucher.startsAt()) : "");
        model.addAttribute("formEndsAt", editingVoucher != null ? formatDateTime(editingVoucher.endsAt()) : "");
        model.addAttribute("formMaxUsage", editingVoucher != null ? editingVoucher.maxUsage() : 0);
        model.addAttribute("formMinOrderValue", editingVoucher != null ? editingVoucher.minOrderValue() : 0);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATETIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String formatDateTime(LocalDateTime value) {
        return value == null ? "" : value.format(DATETIME_FORMATTER);
    }
}

