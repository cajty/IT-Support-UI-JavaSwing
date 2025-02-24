package org.aBly.router;

import com.google.inject.Injector;
import org.aBly.pages.AuthPanel;
import org.aBly.pages.EmployeeDashboard;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RouteManager {
    private static CardLayout cardLayout;
    private static JPanel mainPanel;
    private static Injector injector;
    private static Map<String, Supplier<JPanel>> screenSuppliers = new HashMap<>();
    private static Map<String, JPanel> screens = new HashMap<>();

    public static void init(CardLayout layout, JPanel panel, Injector inj) {
        cardLayout = layout;
        mainPanel = panel;
        injector = inj;
        registerScreens();
    }

    private static void registerScreens() {
        registerScreen("auth", () -> injector.getInstance(AuthPanel.class));
        registerScreen("employeeDashboard", ()->injector.getInstance(EmployeeDashboard.class));

    }

    public static void registerScreen(String name, Supplier<JPanel> panelSupplier) {
        screenSuppliers.put(name, panelSupplier);
    }

    public static void navigate(String name) {
        if (!screens.containsKey(name) && screenSuppliers.containsKey(name)) {
            JPanel panel = screenSuppliers.get(name).get();
            screens.put(name, panel);
            mainPanel.add(panel, name);
        }
        if (screens.containsKey(name)) {
            cardLayout.show(mainPanel, name);
        } else {
            System.err.println("No screen registered with name: " + name);
        }
    }
}