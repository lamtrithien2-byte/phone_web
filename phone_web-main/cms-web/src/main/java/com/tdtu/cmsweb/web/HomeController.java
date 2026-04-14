package com.tdtu.cmsweb.web;

import com.tdtu.cmsweb.api.CategoryApiClient;
import com.tdtu.cmsweb.api.ProductApiClient;
import com.tdtu.cmsweb.api.StatisticsApiClient;
import com.tdtu.cmsweb.api.dto.CategoryView;
import com.tdtu.cmsweb.api.dto.LoginResult;
import com.tdtu.cmsweb.api.dto.ProductView;
import com.tdtu.cmsweb.api.dto.ProductUpsertRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Controller
public class HomeController {

    private final CategoryApiClient categoryApiClient;
    private final ProductApiClient productApiClient;
    private final StatisticsApiClient statisticsApiClient;
    private final com.tdtu.cmsweb.api.VoucherApiClient voucherApiClient;

    public HomeController(CategoryApiClient categoryApiClient,
                          ProductApiClient productApiClient,
                          StatisticsApiClient statisticsApiClient,
                          com.tdtu.cmsweb.api.VoucherApiClient voucherApiClient) {
        this.categoryApiClient = categoryApiClient;
        this.productApiClient = productApiClient;
        this.statisticsApiClient = statisticsApiClient;
        this.voucherApiClient = voucherApiClient;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String success,
                            Model model,
                            HttpSession session) {
        if (SessionSupport.requireLogin(session, model)) {
            return "redirect:/login";
        }
        try {
            bindDashboardModel(model, session, keyword, success);
        } catch (RestClientException ex) {
            bindDashboardFallback(model, session, keyword, success);
            model.addAttribute("error", "Core API is unavailable");
        }
        return "dashboard";
    }

    @GetMapping("/products/manage")
    public String products(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String editBarCode,
                           @RequestParam(required = false) String success,
                           Model model,
                           HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            List<ProductView> products = productApiClient.getProducts(keyword);
            bindProductManagementModel(model, session, keyword, success, products);
            model.addAttribute("editing", editBarCode != null && !editBarCode.isBlank());
            model.addAttribute("productForm", products.stream()
                    .filter(product -> product.barCode().equalsIgnoreCase(editBarCode == null ? "" : editBarCode))
                    .findFirst()
                    .orElse(defaultProductForm()));
        } catch (RestClientException ex) {
            bindProductManagementFallback(model, session, keyword, success);
            model.addAttribute("editing", false);
            model.addAttribute("productForm", defaultProductForm());
            model.addAttribute("error", "Core API is unavailable");
        }
        return "products-management";
    }

    @PostMapping("/dashboard/products/create")
    public String createProduct(@RequestParam String barCode,
                                @RequestParam String name,
                                @RequestParam(required = false) String screenSize,
                                @RequestParam(required = false) String ram,
                                @RequestParam(required = false) String rom,
                                @RequestParam Integer importPrice,
                                @RequestParam Integer priceSale,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String imageLink,
                                @RequestParam Long categoryId,
                                HttpSession session,
                                Model model) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            productApiClient.create(new ProductUpsertRequest(
                    barCode, name, screenSize, ram, rom, importPrice, priceSale,
                    description, imageLink, 0, categoryId
            ));
            return "redirect:/products/manage?success=Da+them+san+pham";
        } catch (RestClientException ex) {
            bindProductManagementFallback(model, session, null, null);
            model.addAttribute("editing", false);
            model.addAttribute("productForm", new ProductView(
                    null, barCode, name, screenSize, ram, rom, importPrice, priceSale,
                    description, imageLink, 0, null, null, false, null
            ));
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("error", "Core API is unavailable");
            return "products-management";
        }
    }

    @PostMapping("/dashboard/products/update")
    public String updateProduct(@RequestParam String barCode,
                                @RequestParam String name,
                                @RequestParam(required = false) String screenSize,
                                @RequestParam(required = false) String ram,
                                @RequestParam(required = false) String rom,
                                @RequestParam Integer importPrice,
                                @RequestParam Integer priceSale,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String imageLink,
                                @RequestParam(defaultValue = "0") Integer saleNumber,
                                @RequestParam Long categoryId,
                                HttpSession session,
                                Model model) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            productApiClient.update(new ProductUpsertRequest(
                    barCode, name, screenSize, ram, rom, importPrice, priceSale,
                    description, imageLink, saleNumber, categoryId
            ));
            return "redirect:/products/manage?success=Da+cap+nhat+san+pham";
        } catch (RestClientException ex) {
            bindProductManagementFallback(model, session, null, null);
            model.addAttribute("editing", true);
            model.addAttribute("productForm", new ProductView(
                    null, barCode, name, screenSize, ram, rom, importPrice, priceSale,
                    description, imageLink, saleNumber, null, null, false, null
            ));
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("error", "Core API is unavailable");
            return "products-management";
        }
    }

    @PostMapping("/dashboard/products/delete")
    public String deleteProduct(@RequestParam String barCode,
                                HttpSession session,
                                Model model) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            productApiClient.delete(barCode);
            return "redirect:/products/manage?success=Da+xoa+san+pham";
        } catch (RestClientException ex) {
            bindProductManagementFallback(model, session, null, null);
            model.addAttribute("editing", false);
            model.addAttribute("productForm", defaultProductForm());
            model.addAttribute("error", "Core API is unavailable");
            return "products-management";
        }
    }

    private void bindDashboardModel(Model model,
                                    HttpSession session,
                                    String keyword,
                                    String success) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        boolean isAdmin = currentUser != null && "admin".equalsIgnoreCase(currentUser.roleName());
        List<com.tdtu.cmsweb.api.dto.StaffRevenueView> revenueRows = statisticsApiClient.getStaffRevenue();
        com.tdtu.cmsweb.api.dto.DashboardSummaryView summary = isAdmin
                ? statisticsApiClient.getDashboardSummary()
                : buildStaffSummary(currentUser, revenueRows);

        model.addAttribute("role", currentUser.roleName());
        SessionSupport.bindCurrentUser(model, session);
        bindNavigation(model);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("success", success);
        model.addAttribute("summary", summary);
        model.addAttribute("staffRevenue", filterRevenueByRole(currentUser, revenueRows));
    }

    private void bindProductManagementModel(Model model,
                                            HttpSession session,
                                            String keyword,
                                            String success,
                                            List<ProductView> products) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        model.addAttribute("role", currentUser.roleName());
        SessionSupport.bindCurrentUser(model, session);
        bindNavigation(model);
        model.addAttribute("activePage", "products");
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("success", success);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryApiClient.getAll());
        model.addAttribute("selectedCategoryId", null);
    }

    private void bindDashboardFallback(Model model,
                                       HttpSession session,
                                       String keyword,
                                       String success) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        model.addAttribute("role", currentUser.roleName());
        SessionSupport.bindCurrentUser(model, session);
        bindNavigation(model);
        model.addAttribute("activePage", "dashboard");
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("success", success);
        model.addAttribute("staffRevenue", java.util.List.of());
        model.addAttribute("summary", currentUser != null && !"admin".equalsIgnoreCase(currentUser.roleName())
                ? new com.tdtu.cmsweb.api.dto.DashboardSummaryView(0L, 0, 0, 0)
                : null);
    }

    private void bindProductManagementFallback(Model model,
                                               HttpSession session,
                                               String keyword,
                                               String success) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        model.addAttribute("role", currentUser.roleName());
        SessionSupport.bindCurrentUser(model, session);
        bindNavigation(model);
        model.addAttribute("activePage", "products");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("success", success);
        model.addAttribute("products", java.util.List.of());
        model.addAttribute("categories", categoryApiClient.getAll());
        model.addAttribute("selectedCategoryId", null);
    }

    private void bindNavigation(Model model) {
        model.addAttribute("dashboardUrl", "/dashboard");
        model.addAttribute("productManagementUrl", "/products/manage");
        model.addAttribute("voucherManagementUrl", "/vouchers");
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("customerLookupUrl", "/customers/lookup");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("shopUrl", "http://localhost:8083/products");
    }

    private List<com.tdtu.cmsweb.api.dto.StaffRevenueView> filterRevenueByRole(LoginResult currentUser,
                                                                                List<com.tdtu.cmsweb.api.dto.StaffRevenueView> revenueRows) {
        if (currentUser == null) {
            return java.util.List.of();
        }
        if ("admin".equalsIgnoreCase(currentUser.roleName())) {
            return revenueRows;
        }
        return revenueRows.stream()
                .filter(item -> item.userId() != null && item.userId().equals(currentUser.id()))
                .toList();
    }

    private com.tdtu.cmsweb.api.dto.DashboardSummaryView buildStaffSummary(LoginResult currentUser,
                                                                           List<com.tdtu.cmsweb.api.dto.StaffRevenueView> revenueRows) {
        if (currentUser == null) {
            return new com.tdtu.cmsweb.api.dto.DashboardSummaryView(0L, 0, 0, 0);
        }
        return revenueRows.stream()
                .filter(item -> item.userId() != null && item.userId().equals(currentUser.id()))
                .findFirst()
                .map(item -> new com.tdtu.cmsweb.api.dto.DashboardSummaryView(
                        item.totalRevenue() != null ? item.totalRevenue() : 0L,
                        item.totalQuantity() != null ? item.totalQuantity() : 0,
                        item.invoiceCount() != null ? item.invoiceCount() : 0,
                        0
                ))
                .orElseGet(() -> new com.tdtu.cmsweb.api.dto.DashboardSummaryView(0L, 0, 0, 0));
    }

    private ProductView defaultProductForm() {
        return new ProductView(null, "", "", "", "", "", 0, 0, "", "", 0, null, null, false, "");
    }
}
