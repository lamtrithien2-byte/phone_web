package com.tdtu.coreapi.category;

import com.tdtu.coreapi.category.dto.CategoryView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CategoryProcedureRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<CategoryView> getAll() {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_category_get_all");
        return query.getResultList().stream()
                .map(value -> {
                    Object[] row = (Object[]) value;
                    return new CategoryView(
                            ((Number) row[0]).longValue(),
                            (String) row[1]
                    );
                })
                .toList();
    }
}
