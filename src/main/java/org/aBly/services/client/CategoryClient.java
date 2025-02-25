package org.aBly.services.client;

import org.aBly.models.category.Category;
import org.aBly.services.CategoryService;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class CategoryClient extends BaseClient {

    private final CategoryService categoryService;

    @Inject
    public CategoryClient(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Optional<List<Category>> getAllCategories() {
        return executeCall(() -> categoryService.getAllCategories().execute());
    }
}