package org.aBly.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.aBly.services.client.AuthClient;
import org.aBly.services.AuthService;
import org.aBly.services.TicketService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppModule extends AbstractModule {

    private static final String BASE_URL = "http://localhost:8080/";

    @Provides
    @Singleton
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
}