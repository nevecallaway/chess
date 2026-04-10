package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDataAccess;
import dataaccess.MySQLDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsErrorContext;
import service.UserService;
import service.ClearService;
import service.GameService;
import service.request.RegisterRequest;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.CreateGameRequest;
import service.request.ListGamesRequest;
import service.request.JoinGameRequest;
import service.result.RegisterResult;
import service.result.LoginResult;
import service.result.CreateGameResult;
import service.result.ListGamesResult;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.ErrorMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadGameMessage;
import java.util.Map;

public class Server {
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;
    private final GameSessionManager sessionManager;
    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        DataAccess dataAccess;
        try {
            DatabaseManager.createDatabase();
            dataAccess = new MySQLDataAccess();
        } catch (DataAccessException ex) {
            System.err.println("Warning: Could not initialize MySQL database, using in-memory storage");
            ex.printStackTrace();
            dataAccess = new MemoryDataAccess();
        }
        this.userService = new UserService(dataAccess);
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.sessionManager = new GameSessionManager(gson);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .post("/game", this::createGame)
                .get("/game", this::listGames)
                .put("/game", this::joinGame)
                .ws("/ws", wsConfig -> {
                    wsConfig.onConnect(this::onWsConnect);
                    wsConfig.onMessage(this::onWsMessage);
                    wsConfig.onClose(this::onWsClose);
                    wsConfig.onError(this::onWsError);
                })
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    public Server(UserService userService, ClearService clearService, GameService gameService) {
        this.userService = userService;
        this.clearService = clearService;
        this.gameService = gameService;
        this.sessionManager = new GameSessionManager(gson);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .post("/game", this::createGame)
                .get("/game", this::listGames)
                .put("/game", this::joinGame)
                .ws("/ws", wsConfig -> {
                    wsConfig.onConnect(this::onWsConnect);
                    wsConfig.onMessage(this::onWsMessage);
                    wsConfig.onClose(this::onWsClose);
                    wsConfig.onError(this::onWsError);
                })
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of()));
    }

    private void register(Context ctx) throws DataAccessException {
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);

        if (request.username() == null || request.password() == null || request.email() == null) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        RegisterResult result = userService.register(request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(result));
    }

    private void login(Context ctx) throws DataAccessException {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

        if (request.username() == null || request.password() == null) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        LoginResult result = userService.login(request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(result));
    }

    private void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        LogoutRequest request = new LogoutRequest(authToken);
        userService.logout(request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of()));
    }

    private void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        CreateGameRequest bodyRequest = gson.fromJson(ctx.body(), CreateGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        if (bodyRequest.gameName() == null || bodyRequest.gameName().isEmpty()) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        CreateGameRequest request = new CreateGameRequest(bodyRequest.gameName(), authToken);
        CreateGameResult result = gameService.createGame(request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(result));
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = gameService.listGames(request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(result));
    }

    private void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        JoinGameRequest bodyRequest = gson.fromJson(ctx.body(), JoinGameRequest.class);

        if (bodyRequest.playerColor() == null || bodyRequest.playerColor().isEmpty()
                || bodyRequest.gameID() <= 0) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
            return;
        }

        JoinGameRequest request = new JoinGameRequest(authToken, bodyRequest.playerColor(), bodyRequest.gameID());
        gameService.joinGame(request);
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of()));
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        String errorMessage = ex.getMessage();
        // Print cause if available for debugging
        if (ex.getCause() != null) {
            errorMessage = ex.getCause().getMessage();
            ex.printStackTrace(); // Print full stack trace for debugging
        }
        
        if (ex.getMessage().contains("already exists") || ex.getMessage().contains("player already taken")) {
            ctx.status(403);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: already taken")));
        } else if (ex.getMessage().contains("Invalid password")
                || ex.getMessage().contains("not found")
                || ex.getMessage().contains("Auth token not found")) {
            ctx.status(401);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } else if (ex.getMessage().contains("Invalid player color")
                || ex.getMessage().contains("Player color is required")
                || ex.getMessage().contains("Game name can't be empty")) {
            ctx.status(400);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } else {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Map.of("message", "Error: " + ex.getMessage())));
        }
    }

    // WebSocket Handlers

    private void onWsConnect(WsConnectContext ctx) {
        // For now, just accept the connection
        // The actual CONNECT command will be handled in onWsMessage
    }

    private void onWsMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

            if (command == null || command.getAuthToken() == null || command.getGameID() == null) {
                sessionManager.sendToUser(ctx, new ErrorMessage("Error: Invalid command format"));
                return;
            }

            switch (command.getCommandType()) {
                case CONNECT:
                    handleConnect(ctx, command);
                    break;
                case MAKE_MOVE:
                    handleMakeMove(ctx, command);
                    break;
                case LEAVE:
                    handleLeave(ctx, command);
                    break;
                case RESIGN:
                    handleResign(ctx, command);
                    break;
                default:
                    sessionManager.sendToUser(ctx, new ErrorMessage("Error: Unknown command type"));
            }
        } catch (Exception e) {
            System.err.println("WebSocket message error: " + e.getMessage());
            e.printStackTrace();
            sessionManager.sendToUser(ctx, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    private void onWsClose(WsCloseContext ctx) {
        GameSessionManager.SessionUser user = sessionManager.getSessionUser(ctx);
        if (user != null) {
            sessionManager.removeUserFromGame(ctx);
            // Notify other players that this user left (they'll broadcast with LEAVE handler)
        }
    }

    private void onWsError(WsErrorContext ctx) {
        System.err.println("WebSocket error: " + ctx.error().getMessage());
        ctx.error().printStackTrace();
        GameSessionManager.SessionUser user = sessionManager.getSessionUser(ctx);
        if (user != null) {
            sessionManager.removeUserFromGame(ctx);
        }
    }

    private void handleConnect(WsMessageContext ctx, UserGameCommand command) {
        // TODO: Implement CONNECT handler
        sessionManager.sendToUser(ctx, new ErrorMessage("Error: CONNECT not yet implemented"));
    }

    private void handleMakeMove(WsMessageContext ctx, UserGameCommand command) {
        // TODO: Implement MAKE_MOVE handler
        sessionManager.sendToUser(ctx, new ErrorMessage("Error: MAKE_MOVE not yet implemented"));
    }

    private void handleLeave(WsMessageContext ctx, UserGameCommand command) {
        // TODO: Implement LEAVE handler
        sessionManager.sendToUser(ctx, new ErrorMessage("Error: LEAVE not yet implemented"));
    }

    private void handleResign(WsMessageContext ctx, UserGameCommand command) {
        // TODO: Implement RESIGN handler
        sessionManager.sendToUser(ctx, new ErrorMessage("Error: RESIGN not yet implemented"));
    }

    public int run(int port) {
        javalin.start(port);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
