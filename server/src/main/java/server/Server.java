package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
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
import java.util.Map;

public class Server {
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;
    private final Javalin javalin;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        this.userService = new UserService(dataAccess);
        this.clearService = new ClearService(dataAccess);
        this.gameService = new GameService(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .post("/game", this::createGame)
                .get("/game", this::listGames)
                .put("/game", this::joinGame)
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    public Server(UserService userService, ClearService clearService, GameService gameService) {
        this.userService = userService;
        this.clearService = clearService;
        this.gameService = gameService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .post("/game", this::createGame)
                .get("/game", this::listGames)
                .put("/game", this::joinGame)
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    private void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        ctx.status(200);
        ctx.json(Map.of());
    }

    private void register(Context ctx) throws DataAccessException {
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);

        if (request.username() == null || request.password() == null || request.email() == null) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        RegisterResult result = userService.register(request);
        ctx.status(200);
        ctx.json(result);
    }

    private void login(Context ctx) throws DataAccessException {
        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);

        if (request.username() == null || request.password() == null) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        LoginResult result = userService.login(request);
        ctx.status(200);
        ctx.json(result);
    }

    private void logout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        LogoutRequest request = new LogoutRequest(authToken);
        userService.logout(request);
        ctx.status(200);
        ctx.json(Map.of());
    }

    private void createGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        CreateGameRequest bodyRequest = new Gson().fromJson(ctx.body(), CreateGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        if (bodyRequest.gameName() == null || bodyRequest.gameName().isEmpty()) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        CreateGameRequest request = new CreateGameRequest(bodyRequest.gameName(), authToken);
        CreateGameResult result = gameService.createGame(request);
        ctx.status(200);
        ctx.json(result);
    }

    private void listGames(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        ListGamesRequest request = new ListGamesRequest(authToken);
        ListGamesResult result = gameService.listGames(request);
        ctx.status(200);
        ctx.json(result);
    }

    private void joinGame(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");

        if (authToken == null || authToken.isEmpty()) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        JoinGameRequest bodyRequest = new Gson().fromJson(ctx.body(), JoinGameRequest.class);

        if (bodyRequest.playerColor() == null || bodyRequest.playerColor().isEmpty()
                || bodyRequest.gameID() == 0) {
            ctx.status(400);
            ctx.json(Map.of("message", "Error: bad request"));
            return;
        }

        JoinGameRequest request = new JoinGameRequest(authToken, bodyRequest.playerColor(), bodyRequest.gameID());
        gameService.joinGame(request);
        ctx.status(200);
        ctx.json(Map.of());
    }

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        if (ex.getMessage().contains("already exists")) {
            ctx.status(403);
            ctx.json(Map.of("message", "Error: already taken"));
        } else if (ex.getMessage().contains("Invalid password")
                || ex.getMessage().contains("not found")
                || ex.getMessage().contains("Auth token not found")) {
            ctx.status(401);
            ctx.json(Map.of("message", "Error: unauthorized"));
        } else {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + ex.getMessage()));
        }
    }

    public int run(int port) {
        javalin.start(port);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
