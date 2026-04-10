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

    public HomeController(CategoryApiClient categoryApiClient,
                          ProductApiClient productApiClient,
                          StatisticsApiClient statisticsApiClient) {
        this.categoryApiClient = categoryApiClient;
        this.productApiClient = productApiClient;
        this.statisticsApiClient = statisticsApiClient;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("admin".equalsIgnoreCase(currentUser.roleName())) {
            return "redirect:/dashboard";
        }
        return "redirect:/sales";
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String editBarCode,
                            @RequestParam(required = false) String success,
                            Model model,
                            HttpSession session) {
        if (SessionSupport.requireAdmin(session, model)) {
            return "redirect:/login";
        }
        try {
            List<ProductView> products = productApiClient.getProducts(keyword);
            bindDashboardModel(model, session, keyword, success, products);
            model.addAttribute("editing", editBarCode != null && !editBarCode.isBlank());
            model.addAttribute("productForm", products.stream()
                    .filter(product -> product.barCode().equalsIgnoreCase(editBarCode == null ? "" : editBarCode))
                    .findFirst()
                    .orElse(defaultProductForm()));
        } catch (RestClientException ex) {
            bindDashboardFallback(model, session, keyword, success);
            model.addAttribute("editing", false);
            model.addAttribute("productForm", defaultProductForm());
            model.addAttribute("error", "Core API is unavailable");
        }
        return "dashboard";
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
            return "redirect:/dashboard?success=Da+them+san+pham";
        } catch (RestClientException ex) {
            bindDashboardFallback(model, session, null, null);
            model.addAttribute("editing", false);
            model.addAttribute("productForm", new ProductView(
                    null, barCode, name, screenSize, ram, rom, importPrice, priceSale,
                    description, imageLink, 0, null, null, false, null
            ));
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("error", "Core API is unavailable");
            return "dashboard";
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
            return "redirect:/dashboard?success=Da+cap+nhat+san+pham";
        } catch (RestClientException ex) {
            bindDashboardFallback(model, session, null, null);
            model.addAttribute("editing", true);
            model.addAttribute("productForm", new ProductView(
                    null, barCode, name, screenSize, ram, rom, importPrice, priceSale,
                    description, imageLink, saleNumber, null, null, false, null
            ));
            model.addAttribute("selectedCategoryId", categoryId);
            model.addAttribute("error", "Core API is unavailable");
            return "dashboard";
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
            return "redirect:/dashboard?success=Da+xoa+san+pham";
        } catch (RestClientException ex) {
            bindDashboardFallback(model, session, null, null);
            model.addAttribute("editing", false);
            model.addAttribute("productForm", defaultProductForm());
            model.addAttribute("error", "Core API is unavailable");
            return "dashboard";
        }
    }

    private void bindDashboardModel(Model model,
                                    HttpSession session,
                                    String keyword,
                                    String success,
                                    List<ProductView> products) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        model.addAttribute("role", currentUser.roleName());
        SessionSupport.bindCurrentUser(model, session);
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("shopUrl", "http://127.0.0.1:8083/products");
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("success", success);
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryApiClient.getAll());
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("summary", statisticsApiClient.getDashboardSummary());
        model.addAttribute("staffRevenue", statisticsApiClient.getStaffRevenue());
    }

    private void bindDashboardFallback(Model model,
                                       HttpSession session,
                                       String keyword,
                                       String success) {
        LoginResult currentUser = SessionSupport.getCurrentUser(session);
        model.addAttribute("role", currentUser.roleName());
        SessionSupport.bindCurrentUser(model, session);
        model.addAttribute("salesUrl", "/sales");
        model.addAttribute("employeesUrl", "/employees");
        model.addAttribute("profileUrl", "/profile");
        model.addAttribute("shopUrl", "http://127.0.0.1:8083/products");
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        model.addAttribute("success", success);
        model.addAttribute("products", java.util.List.of());
        model.addAttribute("categories", java.util.List.<CategoryView>of());
        model.addAttribute("selectedCategoryId", null);
        model.addAttribute("staffRevenue", java.util.List.of());
        model.addAttribute("summary", null);
    }

    private ProductView defaultProductForm() {
        return new ProductView(null, "", "", "", "", "", 0, 0, "", "", 0, null, null, false, "");
    }
}
