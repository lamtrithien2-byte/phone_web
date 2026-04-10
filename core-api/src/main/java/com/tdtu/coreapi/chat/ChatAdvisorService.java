package com.tdtu.coreapi.chat;

import com.tdtu.coreapi.product.ProductProcedureRepository;
import com.tdtu.coreapi.product.dto.ProductView;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ChatAdvisorService {

    private final ProductProcedureRepository productProcedureRepository;

    public ChatAdvisorService(ProductProcedureRepository productProcedureRepository) {
        this.productProcedureRepository = productProcedureRepository;
    }

    public String answer(String message) {
        String normalized = message == null ? "" : message.trim().toLowerCase(Locale.ROOT);
        List<ProductView> products = findRelevantProducts(normalized);
        if (products.isEmpty()) {
            products = productProcedureRepository.getAllProducts().stream().limit(3).toList();
        }

        if (normalized.contains("iphone")) {
            return buildRecommendation("Neu ban uu tien iOS, camera va giu gia tot", products);
        }
        if (normalized.contains("samsung") || normalized.contains("android")) {
            return buildRecommendation("Neu ban muon Android manh, man hinh lon va da nhiem tot", products);
        }
        if (normalized.contains("gia") || normalized.contains("re") || normalized.contains("duoi")) {
            List<ProductView> cheapest = productProcedureRepository.getAllProducts().stream()
                    .sorted(Comparator.comparing(ProductView::priceSale))
                    .limit(3)
                    .toList();
            return buildRecommendation("Day la nhom may co gia de tiep can hon trong catalog hien tai", cheapest);
        }
        if (normalized.contains("choi game") || normalized.contains("gaming")) {
            List<ProductView> gaming = productProcedureRepository.getAllProducts().stream()
                    .sorted(Comparator.comparing(ProductView::ram).reversed())
                    .limit(3)
                    .toList();
            return buildRecommendation("Neu ban can may de choi game va da nhiem", gaming);
        }

        return buildRecommendation("Minh tim thay mot vai goi y phu hop tu catalog hien tai", products);
    }

    private List<ProductView> findRelevantProducts(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return productProcedureRepository.getAllProducts().stream().limit(3).toList();
        }
        List<ProductView> results = productProcedureRepository.search(keyword);
        if (!results.isEmpty()) {
            return results.stream().limit(3).toList();
        }
        return productProcedureRepository.getAllProducts().stream()
                .filter(product -> contains(product.name(), keyword)
                        || contains(product.categoryName(), keyword)
                        || contains(product.ram(), keyword)
                        || contains(product.rom(), keyword))
                .limit(3)
                .toList();
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String buildRecommendation(String intro, List<ProductView> products) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String suggestions = products.stream()
                .map(product -> product.name() + " (" + product.ram() + "/" + product.rom() + ", "
                        + formatter.format(product.priceSale()) + " VND)")
                .reduce((left, right) -> left + "; " + right)
                .orElse("catalog hien tai chua co san pham phu hop");
        return intro + ": " + suggestions + ". Neu ban muon, hay noi ro ngan sach, he dieu hanh, RAM/ROM hoac nhu cau chup anh, choi game.";
    }
}
