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
import java.util.regex.Pattern;

public class AuthPanel extends JPanel {
    private final AuthService authService;
    private boolean isSignup;
    private JTextField emailField, nameField;
    private JPasswordField passwordField;
    private JButton actionButton, switchButton;
    private JLabel titleLabel, errorLabel;
    private JPanel namePanel; // Added reference to name panel
    private final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Inject
    public AuthPanel(AuthService authService) {
        this.authService = authService;
        this.isSignup = false;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new MigLayout("wrap, insets 25", "[grow,fill]", "[]20[]"));
        setBackground(new Color(25, 25, 25));

        // Header
        JLabel appTitle = new JLabel("IT Support System", SwingConstants.CENTER);
        appTitle.setFont(new Font("Arial", Font.BOLD, 24));
        appTitle.setForeground(Color.WHITE);
        add(appTitle, "span, gapbottom 30");

        // Auth Container
        JPanel authContainer = new JPanel(new MigLayout("wrap, insets 20, gapy 15", "[grow,fill]", "[]10[]"));
        authContainer.setBackground(new Color(35, 35, 35));

        titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 173, 239));
        authContainer.add(titleLabel, "center, gapbottom 15");

        // Name Field (hidden initially)
        nameField = createInputField("Full Name");
        namePanel = createInputPanel(nameField, "Name:"); // Store reference to panel
        namePanel.setVisible(false); // Hide the entire panel initially
        authContainer.add(namePanel, "hidemode 3");

        // Email Field
        emailField = createInputField("Email Address");
        authContainer.add(createInputPanel(emailField, "Email:"));

        // Password Field
        passwordField = createPasswordField();
        authContainer.add(createInputPanel(passwordField, "Password:"));

        // Error Label
        errorLabel = new JLabel();
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        authContainer.add(errorLabel, "gapy 0");

        // Action Button
        actionButton = createButton("Login", new Color(0, 173, 239));
        actionButton.addActionListener(this::handleAuthAction);
        authContainer.add(actionButton, "gaptop 15");

        // Switch Mode Button
        switchButton = createButton("Switch to Signup", new Color(60, 60, 60));
        switchButton.addActionListener(this::toggleAuthMode);
        authContainer.add(switchButton);

        add(authContainer);
    }

    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(45, 45, 45));
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(45, 45, 45));
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JPanel createInputPanel(JComponent field, String label) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(new Color(35, 35, 35));

        JLabel textLabel = new JLabel(label);
        textLabel.setForeground(Color.WHITE);
        panel.add(textLabel, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleAuthAction(ActionEvent e) {
        if (!validateInputs()) return;

        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (isSignup) {
            RegisterRequest request = new RegisterRequest(
                nameField.getText(),
                email,
                password
            );
            authService.register(request).enqueue(createCallback("Registration successful!", "Registration failed: "));
        } else {
            LoginRequest request = new LoginRequest(email, password);
            authService.login(request).enqueue(createCallback("Login successful!", "Login failed: "));
        }
    }

    private boolean validateInputs() {
        errorLabel.setText("");

        if (isSignup && nameField.getText().trim().isEmpty()) {
            errorLabel.setText("Name is required");
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            errorLabel.setText("Email is required");
            return false;
        }

        if (!EMAIL_REGEX.matcher(emailField.getText()).matches()) {
            errorLabel.setText("Invalid email format");
            return false;
        }

        if (passwordField.getPassword().length == 0) {
            errorLabel.setText("Password is required");
            return false;
        }

        return true;
    }

    private Callback<LoginResponse> createCallback(String successMessage, String errorPrefix) {
        return new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    RouteManager.navigate("employeeDashboard");
                } else {
                    errorLabel.setText(errorPrefix + response.message());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                errorLabel.setText(errorPrefix + t.getMessage());
            }
        };
    }

    private void toggleAuthMode(ActionEvent e) {
        isSignup = !isSignup;
        titleLabel.setText(isSignup ? "REGISTER" : "LOGIN");
        namePanel.setVisible(isSignup); // Show/hide the entire panel
        actionButton.setText(isSignup ? "Register" : "Login");
        switchButton.setText(isSignup ? "Switch to Login" : "Switch to Signup");
        errorLabel.setText("");
        revalidate();
        repaint();
    }
}