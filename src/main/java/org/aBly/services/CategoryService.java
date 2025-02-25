package org.aBly.services;

import org.aBly.models.category.Category;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface CategoryService {

    @GET("/categories")
    Call<List<Category>> getAllCategories();
}
