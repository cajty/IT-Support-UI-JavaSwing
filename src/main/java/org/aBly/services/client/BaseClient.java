package org.aBly.services.client;

import org.aBly.utils.SessionManager;
import retrofit2.Response;

import java.io.IOException;
import java.util.Optional;

public abstract class BaseClient {


    protected <T> Optional<T> executeCall(ApiCall<T> apiCall) {
        if (!isAuthenticated()) return Optional.empty();

        try {
            Response<T> response = apiCall.execute();
            if (response.isSuccessful()) {
                return Optional.ofNullable(response.body());
            } else {
                System.err.println("[ERROR] API call failed. Status: " + response.code());
                return Optional.empty();
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Network issue: " + e.getMessage());
            return Optional.empty();
        }
    }


    protected boolean isAuthenticated() {
        String authToken = SessionManager.getToken();
        if (authToken == null || authToken.isEmpty()) {
            System.err.println("[ERROR] User is not authenticated.");
            return false;
        }
        return true;
    }

    @FunctionalInterface
    protected interface ApiCall<T> {
        Response<T> execute() throws IOException;
    }
}
