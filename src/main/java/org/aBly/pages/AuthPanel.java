package org.aBly.pages;

import com.google.inject.Inject;
import net.miginfocom.swing.MigLayout;
import org.aBly.models.auth.LoginRequest;
import org.aBly.models.auth.LoginResponse;
import org.aBly.models.auth.RegisterRequest;
import org.aBly.router.RouteManager;
import org.aBly.services.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AuthPanel extends JPanel {
    private final AuthService authService;
    private boolean isSignup;
    private JTextField emailField, nameField;
    private JPasswordField passwordField;
    private JButton actionButton, switchButton;
    private JLabel titleLabel;

    @Inject
    public AuthPanel(AuthService authService) {
        this.authService = authService;
        this.isSignup = false; // Default to login mode
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("wrap 2, insets 20", "[grow, center]", "20[center]20[center]20[center]20[center]20"));
        setBackground(new Color(30, 30, 30));

        titleLabel = new JLabel("LOGIN");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, "span, center");

        nameField = createStyledTextField("Enter Name");
        nameField.setVisible(false); // Initially hidden
        add(nameField, "growx, span");

        emailField = createStyledTextField("Enter Email");
        add(emailField, "growx, span");

        passwordField = createStyledPasswordField("Enter Password");
        add(passwordField, "growx, span");

        actionButton = createStyledButton("Login");
        actionButton.addActionListener(this::handleAction);
        add(actionButton, "growx, span, center");

        switchButton = createStyledButton("Switch to Signup");
        switchButton.addActionListener(this::toggleAuthMode);
        add(switchButton, "growx, span, center");
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(50, 50, 50));
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(50, 50, 50));
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private void handleAction(ActionEvent e) {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (isSignup) {
            String name = nameField.getText();
            RegisterRequest registerRequest = new RegisterRequest(name, email, password);
            authService.register(registerRequest).enqueue(handleResponse("Registration successful", "Registration failed"));
        } else {
            LoginRequest loginRequest = new LoginRequest(email, password);
            authService.login(loginRequest).enqueue(handleResponse("Login successful", "Login failed"));
        }
    }

    private void toggleAuthMode(ActionEvent e) {
        isSignup = !isSignup;
        titleLabel.setText(isSignup ? "REGISTER" : "LOGIN");
        nameField.setVisible(isSignup);
        actionButton.setText(isSignup ? "Register" : "Login");
        switchButton.setText(isSignup ? "Switch to Login" : "Switch to Signup");
        revalidate();
        repaint();
    }

    private Callback<LoginResponse> handleResponse(String successMsg, String errorMsg) {
        return new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JOptionPane.showMessageDialog(null, successMsg);
                    RouteManager.navigate("employeeDashboard");
                } else {
                    JOptionPane.showMessageDialog(null, errorMsg + ": " + response.message(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                JOptionPane.showMessageDialog(null, errorMsg + ": " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };
    }
}
