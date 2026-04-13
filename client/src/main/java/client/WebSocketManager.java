package client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import chess.ChessGame;
import chess.ChessMove;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages WebSocket connection to the Chess server for gameplay communication.
 * Uses Observer pattern to notify listeners of incoming server messages.
 */
public class WebSocketManager {
    private WebSocketClientEndpoint clientEndpoint;
    private final List<ServerMessageListener> listeners;
    private final Gson gson;

    public interface ServerMessageListener {
        void onLoadGame(LoadGameMessage message);
        void onError(ErrorMessage message);
        void onNotification(NotificationMessage message);
    }

    public WebSocketManager() {
        this.listeners = new ArrayList<>();
        this.gson = new Gson();
    }

    /**
     * Callback handler for WebSocket messages
     */
    public void handleMessage(String message) {
        try {
            // Parse the message to determine type
            JsonObject jsonObject = com.google.gson.JsonParser.parseString(message).getAsJsonObject();
            ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
            
            if (serverMessage != null && serverMessage.getServerMessageType() != null) {
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME:
                        // Manually deserialize the game field as ChessGame
                        JsonElement gameElement = jsonObject.get("game");
                        ChessGame game = gson.fromJson(gameElement, ChessGame.class);
                        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
                        notifyLoadGame(loadGameMessage);
                        break;
                    case ERROR:
                        ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                        notifyError(errorMessage);
                        break;
                    case NOTIFICATION:
                        NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                        notifyNotification(notificationMessage);
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Connect to the WebSocket server.
     * @throws Exception if connection fails
     */
    public void connect(String serverURL) throws Exception {
        try {
            String url = serverURL.replace("http://", "ws://").replace("https://", "wss://");
            if (!url.endsWith("/ws")) {
                url = url + (url.endsWith("/") ? "" : "/") + "ws";
            }

            URI socketURI = new URI(url);
            clientEndpoint = new WebSocketClientEndpoint(this);
            clientEndpoint.connectToServer(socketURI);
            
        } catch (Exception ex) {
            throw new Exception("WebSocket connection failed: " + ex.getMessage(), ex);
        }
    }

    /**
     * Disconnect from the WebSocket server.
     */
    public void disconnect() {
        try {
            if (clientEndpoint != null) {
                clientEndpoint.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing WebSocket: " + e.getMessage());
        }
    }

    /**
     * Check if connected to the server.
     */
    public boolean isConnected() {
        return clientEndpoint != null && clientEndpoint.isOpen();
    }

    /**
     * Send a UserGameCommand to the server.
     */
    private void sendCommand(UserGameCommand command) throws Exception {
        if (!isConnected()) {
            throw new Exception("WebSocket not connected");
        }
        
        try {
            String json = gson.toJson(command);
            clientEndpoint.sendMessage(json);
        } catch (IOException ex) {
            throw new Exception("Failed to send WebSocket message: " + ex.getMessage(), ex);
        }
    }

    /**
     * Send a CONNECT command.
     */
    public void connect(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.CONNECT,
                authToken,
                gameID
        );
        sendCommand(command);
    }

    /**
     * Send a MAKE_MOVE command.
     */
    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID,
                move
        );
        sendCommand(command);
    }

    /**
     * Send a LEAVE command.
     */
    public void leave(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        sendCommand(command);
    }

    /**
     * Send a RESIGN command.
     */
    public void resign(String authToken, int gameID) throws Exception {
        UserGameCommand command = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );
        sendCommand(command);
    }

    /**
     * Register a listener for server messages.
     */
    public void addListener(ServerMessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener from notifications.
     */
    public void removeListener(ServerMessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notify all listeners of a LOAD_GAME message.
     */
    private void notifyLoadGame(LoadGameMessage message) {
        for (ServerMessageListener listener : new ArrayList<>(listeners)) {
            listener.onLoadGame(message);
        }
    }

    /**
     * Notify all listeners of an ERROR message.
     */
    private void notifyError(ErrorMessage message) {
        for (ServerMessageListener listener : new ArrayList<>(listeners)) {
            listener.onError(message);
        }
    }

    /**
     * Notify all listeners of a NOTIFICATION message.
     */
    private void notifyNotification(NotificationMessage message) {
        for (ServerMessageListener listener : new ArrayList<>(listeners)) {
            listener.onNotification(message);
        }
    }
}
