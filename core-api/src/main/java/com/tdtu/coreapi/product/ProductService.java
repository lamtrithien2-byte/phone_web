package com.tdtu.coreapi.product;

import com.tdtu.coreapi.product.dto.ProductView;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductProcedureRepository productProcedureRepository;

    public ProductService(ProductProcedureRepository productProcedureRepository) {
        this.productProcedureRepository = productProcedureRepository;
    }

    public List<ProductView> getAll() {
        return productProcedureRepository.getAllProducts();
    }

    public List<ProductView> search(String keyword) {
        if (keyword == null || keyword.isBlank() || "all".equalsIgnoreCase(keyword)) {
            return productProcedureRepository.getAllProducts();
        }
        return productProcedureRepository.search(keyword);
    }

    public Long create(ProductEntity product) {
        return productProcedureRepository.create(product);
    }

    public void update(ProductEntity product) {
        productProcedureRepository.update(product);
    }

    public void delete(String barCode) {
        productProcedureRepository.softDelete(barCode);
    }
}
