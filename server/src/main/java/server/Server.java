package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.UserService;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import java.util.Map;

public class Server {
    private final UserService userService;
    private final Javalin javalin;

    public Server() {
        this(new UserService(new MemoryDataAccess()));
    }

    public Server(UserService userService) {
        this.userService = userService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .exception(DataAccessException.class, this::exceptionHandler);
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

    private void exceptionHandler(DataAccessException ex, Context ctx) {
        if (ex.getMessage().contains("already exists")) {
            ctx.status(403);
            ctx.json(Map.of("message", "Error: already taken"));
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
