package org.aBly.services.client;

import org.aBly.models.auth.LoginRequest;
import org.aBly.models.auth.LoginResponse;
import org.aBly.models.auth.RegisterRequest;
import org.aBly.services.AuthService;
import org.aBly.utils.SessionManager;
import retrofit2.Response;

import javax.inject.Inject;
import javax.swing.JOptionPane;
import java.io.IOException;

public class AuthClient extends BaseClient {
    private final AuthService authService;

    @Inject
    public AuthClient(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String email, String password) {
      try {
    Response<LoginResponse> response = authService.login(new LoginRequest(email, password)).execute();

    if (response.isSuccessful()) { // Check HTTP status first!
        LoginResponse loginResponse = response.body();
        if (loginResponse != null && loginResponse.getToken() != null) {
            SessionManager.setToken(loginResponse.getToken());
            showMessage("Login successful", "Success", JOptionPane.INFORMATION_MESSAGE); // Don't leak the token!
            return true;
        }
    } else {
        // Handle HTTP errors (e.g., 401, 500)
        String errorMessage = "Login failed: " + response.code() + " - " + response.message();
        showMessage(errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
} catch (IOException e) {
    System.err.println("[ERROR] Network error: " + e.getMessage());
    showMessage("Network error. Check your connection.", "Error", JOptionPane.ERROR_MESSAGE);
}
return false;
    }

    public boolean register(String name, String email, String password) {
        return executeCall(() -> authService.register(new RegisterRequest(name, email, password)).execute()).isPresent();
    }

    public void logout() {
        SessionManager.setToken(null);
        showMessage("Logged out successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }
}
