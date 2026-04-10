package com.tdtu.coreapi.product;

import com.tdtu.coreapi.common.ApiResponse;
import com.tdtu.coreapi.product.dto.ProductView;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<List<ProductView>> getAll() {
        return ApiResponse.success(productService.getAll());
    }

    @GetMapping("/search/{keyword}")
    public ApiResponse<List<ProductView>> search(@PathVariable String keyword) {
        return ApiResponse.success(productService.search(keyword));
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductView>> searchByQuery(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(productService.search(keyword));
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody ProductEntity product) {
        return ApiResponse.success(productService.create(product));
    }

    @PutMapping
    public ApiResponse<Void> update(@RequestBody ProductEntity product) {
        productService.update(product);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{barCode}")
    public ApiResponse<Void> delete(@PathVariable String barCode) {
        productService.delete(barCode);
        return ApiResponse.success(null);
    }
}
