package org.aBly.guards;


import org.aBly.router.RouteManager;
import org.aBly.utils.SessionManager;

public class AuthGuard {
    public static void checkAccess(String requiredScreen) {
        if (!SessionManager.isAuthenticated()) {
            RouteManager.navigate("Login");
        } else {
            RouteManager.navigate(requiredScreen);
        }
    }
}
