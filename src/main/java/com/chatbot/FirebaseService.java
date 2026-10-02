package com.chatbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class FirebaseService {

    private static final String AUTH_BASE =
            "https://identitytoolkit.googleapis.com/v1/accounts:";

    private final HttpClient httpClient;
    private final Gson gson;

    private String idToken;
    private String refreshToken;
    private String userId;
    private String email;

    public FirebaseService() {
        httpClient = HttpClient.newHttpClient();
        gson = new Gson();
    }

    // =========================================================
    // SIGN UP
    // =========================================================

    public String signUp(String email, String password) throws Exception {

        String url =
                AUTH_BASE
                        + "signUp?key="
                        + FirebaseConfig.API_KEY;

        JsonObject body = new JsonObject();

        body.addProperty("email", email);
        body.addProperty("password", password);
        body.addProperty("returnSecureToken", true);

        String response = post(url, gson.toJson(body));

        saveAuthData(response);

        return "Account created successfully.";
    }

    // =========================================================
    // SIGN IN
    // =========================================================

    public String signIn(String email, String password) throws Exception {

        String url =
                AUTH_BASE
                        + "signInWithPassword?key="
                        + FirebaseConfig.API_KEY;

        JsonObject body = new JsonObject();

        body.addProperty("email", email);
        body.addProperty("password", password);
        body.addProperty("returnSecureToken", true);

        String response = post(url, gson.toJson(body));

        saveAuthData(response);

        return "Login successful.";
    }

    // =========================================================
    // PASSWORD RESET
    // =========================================================

    public String sendPasswordReset(String email) throws Exception {

        String url =
                AUTH_BASE
                        + "sendOobCode?key="
                        + FirebaseConfig.API_KEY;

        JsonObject body = new JsonObject();

        body.addProperty("requestType", "PASSWORD_RESET");
        body.addProperty("email", email);

        post(url, gson.toJson(body));

        return "Password reset email sent.";
    }

    // =========================================================
    // SAVE AUTHENTICATION DATA
    // =========================================================

    private void saveAuthData(String response) {

        JsonObject json =
                JsonParser.parseString(response)
                        .getAsJsonObject();

        idToken = getString(json, "idToken");
        refreshToken = getString(json, "refreshToken");
        userId = getString(json, "localId");
        email = getString(json, "email");
    }

    // =========================================================
    // HTTP POST
    // =========================================================

    private String post(String url, String jsonBody) throws Exception {

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers.ofString(
                                        jsonBody,
                                        StandardCharsets.UTF_8
                                )
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return response.body();
        }

        throw new Exception(
                getFirebaseError(response.body())
        );
    }

    // =========================================================
    // FIREBASE ERROR HANDLING
    // =========================================================

    private String getFirebaseError(String responseBody) {

        try {

            JsonObject json =
                    JsonParser.parseString(responseBody)
                            .getAsJsonObject();

            if (json.has("error")) {

                JsonObject error =
                        json.getAsJsonObject("error");

                String message =
                        error.has("message")
                                ? error.get("message").getAsString()
                                : "Unknown Firebase error.";

                switch (message) {

                    case "EMAIL_EXISTS":
                        return "This email is already registered.";

                    case "EMAIL_NOT_FOUND":
                        return "No account exists with this email.";

                    case "INVALID_PASSWORD":
                    case "INVALID_LOGIN_CREDENTIALS":
                        return "Incorrect email or password.";

                    case "USER_DISABLED":
                        return "This account has been disabled.";

                    case "WEAK_PASSWORD":
                        return "Password must be at least 6 characters.";

                    case "INVALID_EMAIL":
                        return "Please enter a valid email address.";

                    case "TOO_MANY_ATTEMPTS_TRY_LATER":
                        return "Too many attempts. Please try again later.";

                    case "API_KEY_INVALID":
                        return "Firebase API key is invalid.";

                    case "PROJECT_NOT_FOUND":
                        return "Firebase project was not found.";

                    default:
                        return "Firebase error: " + message;
                }
            }

        } catch (Exception ignored) {
        }

        return "Authentication failed.";
    }

    // =========================================================
    // GETTERS
    // =========================================================

    private String getString(
            JsonObject json,
            String key
    ) {

        if (json.has(key)
                && !json.get(key).isJsonNull()) {

            return json.get(key).getAsString();
        }

        return null;
    }

    public boolean isSignedIn() {
        return idToken != null
                && !idToken.isBlank();
    }

    public String getIdToken() {
        return idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    // =========================================================
    // REALTIME DATABASE - READ
    // =========================================================

    public String read(String path) throws Exception {

        if (!isSignedIn()) {
            throw new Exception("You are not signed in.");
        }

        String cleanPath = path;

        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        String url =
                FirebaseConfig.DATABASE_URL
                        + "/"
                        + cleanPath
                        + ".json?auth="
                        + idToken;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return response.body();
        }

        throw new Exception(
                "Database read failed: "
                        + response.body()
        );
    }

    // =========================================================
    // REALTIME DATABASE - WRITE
    // =========================================================

    public String write(
            String path,
            String json
    ) throws Exception {

        if (!isSignedIn()) {
            throw new Exception("You are not signed in.");
        }

        String cleanPath = path;

        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        String url =
                FirebaseConfig.DATABASE_URL
                        + "/"
                        + cleanPath
                        + ".json?auth="
                        + idToken;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers.ofString(
                                        json,
                                        StandardCharsets.UTF_8
                                )
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return response.body();
        }

        throw new Exception(
                "Database write failed: "
                        + response.body()
        );
    }

    // =========================================================
    // REALTIME DATABASE - DELETE
    // =========================================================

    public void delete(String path) throws Exception {

        if (!isSignedIn()) {
            throw new Exception("You are not signed in.");
        }

        String cleanPath = path;

        if (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        String url =
                FirebaseConfig.DATABASE_URL
                        + "/"
                        + cleanPath
                        + ".json?auth="
                        + idToken;

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .DELETE()
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new Exception(
                    "Database delete failed: "
                            + response.body()
            );
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    public void logout() {

        idToken = null;
        refreshToken = null;
        userId = null;
        email = null;
    }
}