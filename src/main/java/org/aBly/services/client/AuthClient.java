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

public class AuthClient {
    private final AuthService authService;

    @Inject
    public AuthClient(AuthService authService) {
        this.authService = authService;
    }

    public boolean login(String email, String password) {
        try {
            Response<LoginResponse> response = authService.login(new LoginRequest(email, password)).execute();
            if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                System.out.println(response.body().getToken());
                SessionManager.setToken(response.body().getToken());
                showMessage(" successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            showMessage("Login failed. Please check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("An error occurred during login.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean register(String name, String email, String password) {
        try {
            Response<LoginResponse> response = authService.register(new RegisterRequest(name, email, password)).execute();
            if (response.isSuccessful() && response.body() != null && response.body().getToken() != null) {
                showMessage("Registration successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            showMessage("Registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("An error occurred during registration.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public void logout() {
        SessionManager.setToken(null);
        showMessage("Logged out successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(null, message, title, type);
    }
}

