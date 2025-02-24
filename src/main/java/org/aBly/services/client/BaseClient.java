package org.aBly.services.client;

import org.aBly.interceptors.ErrorInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import org.aBly.interceptors.TokenInterceptor;
import org.aBly.utils.SessionManager;

public abstract class BaseClient {
    protected static final String BASE_URL = "http://localhost:8080/";

    protected static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build();

   private static OkHttpClient getClient() {
    String authToken = SessionManager.getToken();
    System.out.println("Auth Token retrieved: " + (authToken == null ? "NULL" : "PRESENT"));

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new TokenInterceptor(authToken))
            .addInterceptor(new ErrorInterceptor()) // Ensure ErrorInterceptor is added
            .build();

    System.out.println("TokenInterceptor added");
    return client;
}

}