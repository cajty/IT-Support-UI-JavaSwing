package org.aBly.services;

import org.aBly.models.auth.LoginRequest;
import org.aBly.models.auth.LoginResponse;
import org.aBly.models.auth.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/signup")
    Call<LoginResponse> register(@Body RegisterRequest request);
}

