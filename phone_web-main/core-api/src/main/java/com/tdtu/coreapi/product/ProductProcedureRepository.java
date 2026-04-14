package com.tdtu.coreapi.product;

import com.tdtu.coreapi.product.dto.ProductView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductView> getAllProducts() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_product_get_all");
        return map(query.getResultList());
    }

    public List<ProductView> search(String keyword) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_product_search");
        query.registerStoredProcedureParameter("p_keyword", String.class, ParameterMode.IN);
        query.setParameter("p_keyword", keyword);
        return map(query.getResultList());
    }

    public Long create(ProductEntity product) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_product_create");
        query.registerStoredProcedureParameter("p_bar_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_screen_size", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_ram", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_rom", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_import_price", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_price_sale", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_description", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_image_link", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_category_id", Long.class, ParameterMode.IN);

        query.setParameter("p_bar_code", product.getBarCode());
        query.setParameter("p_name", product.getName());
        query.setParameter("p_screen_size", product.getScreenSize());
        query.setParameter("p_ram", product.getRam());
        query.setParameter("p_rom", product.getRom());
        query.setParameter("p_import_price", product.getImportPrice());
        query.setParameter("p_price_sale", product.getPriceSale());
        query.setParameter("p_description", product.getDescription());
        query.setParameter("p_image_link", product.getImageLink());
        query.setParameter("p_category_id", product.getCategoryId());

        Object result = query.getSingleResult();
        return ((Number) result).longValue();
    }

    public void update(ProductEntity product) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_product_update");
        query.registerStoredProcedureParameter("p_bar_code", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_name", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_screen_size", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_import_price", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_price_sale", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_ram", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_rom", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_description", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_sale_number", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_image_link", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_category_id", Long.class, ParameterMode.IN);

        query.setParameter("p_bar_code", product.getBarCode());
        query.setParameter("p_name", product.getName());
        query.setParameter("p_screen_size", product.getScreenSize());
        query.setParameter("p_import_price", product.getImportPrice());
        query.setParameter("p_price_sale", product.getPriceSale());
        query.setParameter("p_ram", product.getRam());
        query.setParameter("p_rom", product.getRom());
        query.setParameter("p_description", product.getDescription());
        query.setParameter("p_sale_number", product.getSaleNumber());
        query.setParameter("p_image_link", product.getImageLink());
        query.setParameter("p_category_id", product.getCategoryId());

        query.execute();
    }

    public void softDelete(String barCode) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_product_soft_delete");
        query.registerStoredProcedureParameter("p_bar_code", String.class, ParameterMode.IN);
        query.setParameter("p_bar_code", barCode);
        query.execute();
    }

    private List<ProductView> map(List<Object[]> rows) {
        return rows.stream().map(row -> new ProductView(
                ((Number) row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (String) row[5],
                ((Number) row[6]).intValue(),
                ((Number) row[7]).intValue(),
                (String) row[8],
                (String) row[9],
                ((Number) row[10]).intValue(),
                (java.util.Date) row[11],
                (java.util.Date) row[12],
                toBoolean(row[13]),
                (String) row[14]
        )).toList();
    }

    private Boolean toBoolean(Object value) {
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        return value != null && Boolean.parseBoolean(value.toString());
    }
}
