package org.aBly.utils;

public class SessionManager {
    private static String authToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRU1QTE9ZRUUiLCJzdWIiOiJ0aGFua2FsYWhAZ21haWwuY29tIiwiaWF0IjoxNzQwNDMzMTIwLCJleHAiOjE3NDA1MTk1MjB9.2g964IaxxqwOC2CQ9oq_Q4sFtqnw5Gf8K1LYvdXA74Y";

    public static void setToken(String token) {
        System.out.println(authToken);
        authToken = token; }
    public static String getToken() { return authToken; }
    public static boolean isAuthenticated() { return authToken != null; }
}