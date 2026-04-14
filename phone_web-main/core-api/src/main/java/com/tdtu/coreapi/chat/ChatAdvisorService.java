package com.tdtu.coreapi.chat;

import com.tdtu.coreapi.product.ProductProcedureRepository;
import com.tdtu.coreapi.product.dto.ProductView;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatAdvisorService {

    private static final Locale VIETNAMESE = new Locale("vi", "VN");

    private final ProductProcedureRepository productProcedureRepository;

    public ChatAdvisorService(ProductProcedureRepository productProcedureRepository) {
        this.productProcedureRepository = productProcedureRepository;
    }

    public String answer(String message) {
        String normalized = normalize(message);
        List<ProductView> allProducts = productProcedureRepository.getAllProducts();
        if (allProducts.isEmpty()) {
            return "Hiện tại danh mục chưa có sản phẩm để tư vấn.";
        }

        SearchCriteria criteria = extractCriteria(normalized);
        List<ProductView> exactMatches = allProducts.stream()
                .filter(product -> matches(product, criteria))
                .sorted(buildComparator(criteria))
                .limit(3)
                .toList();

        if (!exactMatches.isEmpty()) {
            return buildRecommendation(criteria, exactMatches, true);
        }

        List<ProductView> relaxedMatches = allProducts.stream()
                .sorted(buildComparator(criteria))
                .limit(3)
                .toList();
        return buildRecommendation(criteria, relaxedMatches, false);
    }

    private Comparator<ProductView> buildComparator(SearchCriteria criteria) {
        return Comparator
                .comparingInt((ProductView product) -> score(product, criteria)).reversed()
                .thenComparing(product -> distanceFromBudget(product, criteria))
                .thenComparing(ProductView::priceSale, Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private boolean matches(ProductView product, SearchCriteria criteria) {
        if (criteria.maxPrice() != null && safePrice(product) > criteria.maxPrice()) {
            return false;
        }
        if (criteria.minPrice() != null && safePrice(product) < criteria.minPrice()) {
            return false;
        }
        if (criteria.requireIphone() && !contains(product.name(), "iphone") && !contains(product.categoryName(), "iphone")) {
            return false;
        }
        if (criteria.requireAndroid() && contains(product.name(), "iphone")) {
            return false;
        }
        if (criteria.preferredBrands().contains("samsung") && !contains(product.name(), "samsung")) {
            return false;
        }
        if (criteria.preferredBrands().contains("xiaomi") && !contains(product.name(), "xiaomi")) {
            return false;
        }
        if (criteria.preferredBrands().contains("apple") && !contains(product.name(), "iphone")) {
            return false;
        }
        if (criteria.minRam() != null && parseStorageValue(product.ram()) < criteria.minRam()) {
            return false;
        }
        if (criteria.minRom() != null && parseStorageValue(product.rom()) < criteria.minRom()) {
            return false;
        }
        return true;
    }

    private int score(ProductView product, SearchCriteria criteria) {
        int score = 0;
        int price = safePrice(product);

        if (criteria.maxPrice() != null && price <= criteria.maxPrice()) {
            score += 30;
        }
        if (criteria.minPrice() != null && price >= criteria.minPrice()) {
            score += 15;
        }
        if (criteria.requireIphone() && (contains(product.name(), "iphone") || contains(product.categoryName(), "iphone"))) {
            score += 40;
        }
        if (criteria.requireAndroid() && !contains(product.name(), "iphone")) {
            score += 35;
        }
        if (criteria.preferredBrands().contains("samsung") && contains(product.name(), "samsung")) {
            score += 40;
        }
        if (criteria.preferredBrands().contains("xiaomi") && contains(product.name(), "xiaomi")) {
            score += 40;
        }
        if (criteria.preferredBrands().contains("apple") && contains(product.name(), "iphone")) {
            score += 40;
        }
        if (criteria.minRam() != null && parseStorageValue(product.ram()) >= criteria.minRam()) {
            score += 20;
        }
        if (criteria.minRom() != null && parseStorageValue(product.rom()) >= criteria.minRom()) {
            score += 15;
        }
        if (criteria.needGaming()) {
            score += parseStorageValue(product.ram()) * 2;
            if (contains(product.categoryName(), "flagship")) {
                score += 20;
            }
        }
        if (criteria.needCamera() && contains(product.description(), "flagship")) {
            score += 20;
        }
        if (criteria.needBattery() && contains(product.description(), "ultra")) {
            score += 10;
        }
        if (criteria.needCheap()) {
            score += Math.max(0, 25 - (price / 1_000_000));
        }
        if (criteria.rawKeyword() != null && !criteria.rawKeyword().isBlank()) {
            if (contains(product.name(), criteria.rawKeyword()) || contains(product.categoryName(), criteria.rawKeyword())) {
                score += 25;
            }
        }
        score += product.saleNumber() != null ? product.saleNumber() : 0;
        return score;
    }

    private int distanceFromBudget(ProductView product, SearchCriteria criteria) {
        int price = safePrice(product);
        if (criteria.maxPrice() != null) {
            return Math.abs(criteria.maxPrice() - price);
        }
        if (criteria.minPrice() != null) {
            return Math.abs(criteria.minPrice() - price);
        }
        return price;
    }

    private String buildRecommendation(SearchCriteria criteria, List<ProductView> products, boolean exact) {
        NumberFormat formatter = NumberFormat.getNumberInstance(VIETNAMESE);
        List<String> matchedHints = buildMatchedHints(criteria);
        String intro = exact
                ? "Mình đã lọc từ dữ liệu sản phẩm hiện có"
                : "Chưa có máy khớp hoàn toàn, mình đề xuất các máy gần nhất trong dữ liệu hiện có";
        String hintText = matchedHints.isEmpty() ? "" : " theo yêu cầu " + String.join(", ", matchedHints);

        String suggestions = products.stream()
                .map(product -> product.name()
                        + " - " + formatter.format(safePrice(product)) + " VND"
                        + " | " + defaultString(product.categoryName())
                        + " | RAM " + defaultString(product.ram())
                        + " | ROM " + defaultString(product.rom()))
                .reduce((left, right) -> left + "; " + right)
                .orElse("chưa có sản phẩm phù hợp");

        String followUp = "Bạn có thể nhắn thêm yêu cầu như dưới 15 triệu, iPhone hoặc Android, RAM 8GB+, ROM 256GB, chơi game, chụp ảnh.";
        return intro + hintText + ": " + suggestions + ". " + followUp;
    }

    private List<String> buildMatchedHints(SearchCriteria criteria) {
        List<String> hints = new ArrayList<>();
        if (criteria.maxPrice() != null) {
            hints.add("giá tối đa " + formatMoney(criteria.maxPrice()));
        }
        if (criteria.minPrice() != null) {
            hints.add("giá từ " + formatMoney(criteria.minPrice()));
        }
        if (criteria.requireIphone()) {
            hints.add("iPhone");
        }
        if (criteria.requireAndroid()) {
            hints.add("Android");
        }
        if (!criteria.preferredBrands().isEmpty()) {
            hints.addAll(criteria.preferredBrands());
        }
        if (criteria.minRam() != null) {
            hints.add("RAM từ " + criteria.minRam() + "GB");
        }
        if (criteria.minRom() != null) {
            hints.add("ROM từ " + criteria.minRom() + "GB");
        }
        if (criteria.needGaming()) {
            hints.add("chơi game");
        }
        if (criteria.needCamera()) {
            hints.add("chụp ảnh");
        }
        if (criteria.needBattery()) {
            hints.add("pin");
        }
        return hints;
    }

    private SearchCriteria extractCriteria(String normalized) {
        Integer maxPrice = extractBudget(normalized, true);
        Integer minPrice = extractBudget(normalized, false);
        Integer ram = extractStorage(normalized, "(?:ram\\s*)?(\\d{1,2})\\s*gb");
        Integer rom = extractStorage(normalized, "(?:rom|bo nho|bo nho trong)\\s*(\\d{2,4})\\s*gb");

        if (rom == null) {
            Matcher matcher = Pattern.compile("(\\d{2,4})\\s*gb").matcher(normalized);
            while (matcher.find()) {
                int value = Integer.parseInt(matcher.group(1));
                if (value >= 64) {
                    rom = value;
                    break;
                }
            }
        }

        Set<String> brands = new LinkedHashSet<>();
        if (normalized.contains("samsung")) {
            brands.add("Samsung");
        }
        if (normalized.contains("xiaomi")) {
            brands.add("Xiaomi");
        }
        if (normalized.contains("apple")) {
            brands.add("Apple");
        }

        boolean requireIphone = normalized.contains("iphone") || normalized.contains("ios");
        boolean requireAndroid = normalized.contains("android") && !requireIphone;
        boolean needGaming = normalized.contains("choi game") || normalized.contains("gaming") || normalized.contains("manh");
        boolean needCamera = normalized.contains("camera") || normalized.contains("chup anh") || normalized.contains("quay video");
        boolean needBattery = normalized.contains("pin") || normalized.contains("trau");
        boolean needCheap = normalized.contains("re") || normalized.contains("tiet kiem") || normalized.contains("duoi");

        return new SearchCriteria(
                maxPrice,
                minPrice,
                requireIphone,
                requireAndroid,
                brands,
                ram,
                rom,
                needGaming,
                needCamera,
                needBattery,
                needCheap,
                extractRawKeyword(normalized)
        );
    }

    private Integer extractBudget(String normalized, boolean maxBudget) {
        Matcher duoiMatcher = Pattern.compile("(duoi|toi da|khong qua)\\s*(\\d+(?:[\\.,]\\d+)?)\\s*(tr|trieu|m|k)?").matcher(normalized);
        if (maxBudget && duoiMatcher.find()) {
            return convertMoney(duoiMatcher.group(2), duoiMatcher.group(3));
        }

        Matcher trenMatcher = Pattern.compile("(tren|tu)\\s*(\\d+(?:[\\.,]\\d+)?)\\s*(tr|trieu|m|k)?").matcher(normalized);
        if (!maxBudget && trenMatcher.find()) {
            return convertMoney(trenMatcher.group(2), trenMatcher.group(3));
        }

        Matcher rangeMatcher = Pattern.compile("(\\d+(?:[\\.,]\\d+)?)\\s*(tr|trieu|m|k)\\s*(den|toi)\\s*(\\d+(?:[\\.,]\\d+)?)\\s*(tr|trieu|m|k)?").matcher(normalized);
        if (rangeMatcher.find()) {
            return maxBudget
                    ? convertMoney(rangeMatcher.group(4), rangeMatcher.group(5) != null ? rangeMatcher.group(5) : rangeMatcher.group(2))
                    : convertMoney(rangeMatcher.group(1), rangeMatcher.group(2));
        }
        return null;
    }

    private Integer extractStorage(String normalized, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(normalized);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }

    private String extractRawKeyword(String normalized) {
        return normalized == null ? "" : normalized;
    }

    private int convertMoney(String number, String unit) {
        double value = Double.parseDouble(number.replace(",", "."));
        String normalizedUnit = unit == null ? "" : unit;
        if (normalizedUnit.startsWith("k")) {
            return (int) Math.round(value * 1_000);
        }
        if (normalizedUnit.startsWith("tr") || normalizedUnit.startsWith("m")) {
            return (int) Math.round(value * 1_000_000);
        }
        if (value < 1_000) {
            return (int) Math.round(value * 1_000_000);
        }
        return (int) Math.round(value);
    }

    private int parseStorageValue(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        Matcher matcher = Pattern.compile("(\\d{1,4})").matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return 0;
    }

    private boolean contains(String value, String keyword) {
        if (value == null || keyword == null || keyword.isBlank()) {
            return false;
        }
        return normalize(value).contains(normalize(keyword));
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return decomposed.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private int safePrice(ProductView product) {
        return product.priceSale() != null ? product.priceSale() : 0;
    }

    private String formatMoney(int value) {
        return NumberFormat.getNumberInstance(VIETNAMESE).format(value) + " VND";
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private record SearchCriteria(
            Integer maxPrice,
            Integer minPrice,
            boolean requireIphone,
            boolean requireAndroid,
            Set<String> preferredBrands,
            Integer minRam,
            Integer minRom,
            boolean needGaming,
            boolean needCamera,
            boolean needBattery,
            boolean needCheap,
            String rawKeyword
    ) {
    }
}
