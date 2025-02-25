package org.aBly;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.aBly.di.AppModule;
import org.aBly.router.RouteManager;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private final Injector injector;

    public MainApp() {
        injector = Guice.createInjector(new AppModule());  // ✅ Correct Guice Initialization

        setTitle("IT Support Ticket System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set application icon
        setAppIcon();

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        RouteManager.init(cardLayout, mainPanel, injector);  // ✅ Pass Injector

        add(mainPanel);
        setVisible(true);

        RouteManager.navigate("auth");
    }

    private void setAppIcon() {
        try {

            ImageIcon icon = new ImageIcon(getClass().getResource("/appIcon.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
