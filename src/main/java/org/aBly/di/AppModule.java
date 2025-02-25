package org.aBly.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import okhttp3.OkHttpClient;
import org.aBly.interceptors.ErrorInterceptor;
import org.aBly.interceptors.TokenInterceptor;
import org.aBly.services.client.AuthClient;
import org.aBly.services.client.CategoryClient;
import org.aBly.services.AuthService;
import org.aBly.services.CategoryService;
import org.aBly.services.TicketService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppModule extends AbstractModule {

    private static final String BASE_URL = "http://localhost:8080/";

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new TokenInterceptor())
                .addInterceptor(new ErrorInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public AuthService provideAuthService(Retrofit retrofit) {
        return retrofit.create(AuthService.class);
    }

    @Provides
    @Singleton
    public AuthClient provideAuthAPI(AuthService authService) {
        return new AuthClient(authService);
    }

    @Provides
    @Singleton
    public TicketService provideTicketService(Retrofit retrofit) {
        return retrofit.create(TicketService.class);
    }

    @Provides
    @Singleton
    public CategoryService provideCategoryService(Retrofit retrofit) {
        return retrofit.create(CategoryService.class);
    }

    @Provides
    @Singleton
    public CategoryClient provideCategoryClient(CategoryService categoryService) {
        return new CategoryClient(categoryService);
    }
}