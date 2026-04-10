package server;

import io.javalin.websocket.WsContext;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages WebSocket connections for active games.
 * Tracks which users are connected to which games and handles message broadcasting.
 */
public class GameSessionManager {
    private final Gson gson;
    
    // gameID -> set of WebSocket connections for that game
    private final Map<Integer, Set<WsContext>> gameSessions = new ConcurrentHashMap<>();
    
    // WsContext -> user info (username, gameID, authToken)
    private final Map<WsContext, SessionUser> sessionUsers = new ConcurrentHashMap<>();

    public GameSessionManager(Gson gson) {
        this.gson = gson;
    }

    /**
     * Register a new WebSocket connection for a game
     */
    public void addUserToGame(Integer gameID, WsContext ctx, String username, String authToken) {
        gameSessions.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(ctx);
        sessionUsers.put(ctx, new SessionUser(username, gameID, authToken));
    }

    /**
     * Remove a user from their game session when they disconnect
     */
    public void removeUserFromGame(WsContext ctx) {
        SessionUser user = sessionUsers.remove(ctx);
        if (user != null) {
            Set<WsContext> sessions = gameSessions.get(user.gameID);
            if (sessions != null) {
                sessions.remove(ctx);
                if (sessions.isEmpty()) {
                    gameSessions.remove(user.gameID);
                }
            }
        }
    }

    /**
     * Get user info for a WebSocket connection
     */
    public SessionUser getSessionUser(WsContext ctx) {
        return sessionUsers.get(ctx);
    }

    /**
     * Broadcast a message to all users in a specific game
     */
    public void broadcastToGame(Integer gameID, ServerMessage message) {
        Set<WsContext> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            String json = gson.toJson(message);
            for (WsContext ctx : sessions) {
                try {
                    ctx.send(json);
                } catch (Exception e) {
                    System.err.println("Error sending message to game " + gameID + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Send a message to all users EXCEPT the sender
     */
    public void broadcastToGameExcept(Integer gameID, ServerMessage message, WsContext sender) {
        Set<WsContext> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            String json = gson.toJson(message);
            for (WsContext ctx : sessions) {
                if (!ctx.equals(sender)) {
                    try {
                        ctx.send(json);
                    } catch (Exception e) {
                        System.err.println("Error sending message to game " + gameID + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Send a message to only one user
     */
    public void sendToUser(WsContext ctx, ServerMessage message) {
        try {
            ctx.send(gson.toJson(message));
        } catch (Exception e) {
            System.err.println("Error sending message to user: " + e.getMessage());
        }
    }

    /**
     * Get all connected users for a game
     */
    public Set<WsContext> getGameSessions(Integer gameID) {
        return gameSessions.getOrDefault(gameID, new HashSet<>());
    }

    /**
     * Check if any users are connected to a game
     */
    public boolean hasActiveSessions(Integer gameID) {
        Set<WsContext> sessions = gameSessions.get(gameID);
        return sessions != null && !sessions.isEmpty();
    }

    /**
     * Represents a user in an active game session
     */
    public static class SessionUser {
        public final String username;
        public final Integer gameID;
        public final String authToken;

        public SessionUser(String username, Integer gameID, String authToken) {
            this.username = username;
            this.gameID = gameID;
            this.authToken = authToken;
        }
    }
}
