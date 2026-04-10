package com.tdtu.coreapi.category;

import com.tdtu.coreapi.category.dto.CategoryView;
import com.tdtu.coreapi.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryProcedureRepository categoryProcedureRepository;

    public CategoryController(CategoryProcedureRepository categoryProcedureRepository) {
        this.categoryProcedureRepository = categoryProcedureRepository;
    }

    @GetMapping
    public ApiResponse<List<CategoryView>> getAll() {
        return ApiResponse.success(categoryProcedureRepository.getAll());
    }
}
