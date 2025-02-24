package org.aBly.interceptors;

import com.google.gson.Gson;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ErrorInterceptor implements Interceptor {
    private final Gson gson = new Gson();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        try {
            Response response = chain.proceed(request);

            if (!response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String responseBodyString = responseBody != null ? responseBody.string() : "";

                ApiErrorResponse errorResponse = null;
                try {
                    errorResponse = gson.fromJson(responseBodyString, ApiErrorResponse.class);
                } catch (Exception e) {

                }

                if (errorResponse != null) {
                    handleApiError(response.code(), errorResponse);
                } else {
                    handleUnknownError(response.code(), responseBodyString);
                }
            }

            return response;
        } catch (ConnectException e) {
            handleConnectionError("Failed to connect to the server. Please check if the server is running.");
            throw e;
        } catch (SocketTimeoutException e) {
            handleConnectionError("The connection timed out. Please try again later.");
            throw e;
        } catch (UnknownHostException e) {
            handleConnectionError("Could not resolve server address. Please check your internet connection.");
            throw e;
        }
    }

    private void handleApiError(int statusCode, ApiErrorResponse errorResponse) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("Error: ").append(errorResponse.getMessage()).append("\n");

        if (errorResponse.getValidationErrors() != null) {
            errorMessage.append("Validation Errors:\n");
            errorResponse.getValidationErrors().forEach((field, error) ->
                    errorMessage.append(field).append(": ").append(error).append("\n")
            );
        }

        JOptionPane.showMessageDialog(null, errorMessage.toString(), "API Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleUnknownError(int statusCode, String responseBody) {
        String errorMessage = "Unknown error occurred. HTTP Status Code: " + statusCode +
                "\nResponse Body: " + responseBody;
        JOptionPane.showMessageDialog(null, errorMessage, "API Error", JOptionPane.ERROR_MESSAGE);
    }

    private void handleConnectionError(String message) {
        JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
    }
}
