package client;

import com.google.gson.Gson;
import service.request.*;
import service.result.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class ServerFacade {
    private final int port;
    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(int port) {
        this.port = port;
        this.serverUrl = "http://localhost:" + port;
        this.gson = new Gson();
    }

    public RegisterResult register(String username, String password, String email) throws Exception {
        RegisterRequest request = new RegisterRequest(username, password, email);
        return makeRequest("POST", "/user", request, RegisterResult.class, null);
    }

    public LoginResult login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        return makeRequest("POST", "/session", request, LoginResult.class, null);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, Void.class, authToken);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws Exception {
        CreateGameRequest request = new CreateGameRequest(gameName, authToken);
        return makeRequest("POST", "/game", request, CreateGameResult.class, authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, ListGamesResult.class, authToken);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws Exception {
        // Create request body without authToken (authToken goes in Authorization header)
        JoinGameBody request = new JoinGameBody(playerColor, gameID);
        makeRequest("PUT", "/game", request, Void.class, authToken);
    }

    // Inner class for join game request body (without authToken)
    private static class JoinGameBody {
        String playerColor;
        int gameID;

        JoinGameBody(String playerColor, int gameID) {
            this.playerColor = playerColor;
            this.gameID = gameID;
        }
    }

    public void clear() throws Exception {
        makeRequest("DELETE", "/db", null, Void.class, null);
    }

    private <T> T makeRequest(String method, String path, Object requestBody, Class<T> responseType, String authToken) throws Exception {
        try {
            URI uri = new URI(serverUrl + path);
            HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
            http.setRequestMethod(method);
            http.setConnectTimeout(5000);
            http.setReadTimeout(5000);

            // Set headers
            http.setRequestProperty("Content-Type", "application/json");
            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }

            // Send request body if present
            if (requestBody != null && !method.equals("GET") && !method.equals("DELETE")) {
                http.setDoOutput(true);
                String jsonBody = gson.toJson(requestBody);
                try (var os = http.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }
            }

            // Handle response
            int responseCode = http.getResponseCode();
            if (responseCode == 200) {
                if (responseType == Void.class) {
                    return null;
                }
                try (var is = http.getInputStream()) {
                    String responseBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    return gson.fromJson(responseBody, responseType);
                }
            } else {
                // Read error response
                try (var es = http.getErrorStream()) {
                    String errorBody = new String(es.readAllBytes(), StandardCharsets.UTF_8);
                    throw new Exception("HTTP " + responseCode + ": " + errorBody);
                }
            }
        } catch (URISyntaxException e) {
            throw new Exception("Invalid URI: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new Exception("Network error: " + e.getMessage(), e);
        }
    }
}
