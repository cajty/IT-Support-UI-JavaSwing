package org.aBly.interceptors;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class TokenInterceptor implements Interceptor {
    private final String authToken;

    public TokenInterceptor(String authToken) {
        this.authToken = authToken;
    }

  @Override
public Response intercept(Chain chain) throws IOException {
        System.err.println("Missing or empty authentication token");
    Request originalRequest = chain.request();
    if (authToken == null || authToken.isEmpty()) {
        System.err.println("Missing or empty authentication token");
        return chain.proceed(originalRequest);
    }
    Request newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer " + authToken)
            .build();
    return chain.proceed(newRequest);
}
}

