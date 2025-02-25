package org.aBly.utils;

public class SessionManager {
    private static String authToken ;

    public static void setToken(String token) {
        System.out.println(authToken);
        authToken = token; }
    public static String getToken() { return authToken; }
    public static boolean isAuthenticated() { return authToken != null; }
}