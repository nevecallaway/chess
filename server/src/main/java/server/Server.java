package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.UserService;
import service.ClearService;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import java.util.Map;

public class Server {
    private final UserService userService;
    private final ClearService clearService;
    private final Javalin javalin;

    public Server() {
        DataAccess dataAccess = new MemoryDataAccess();
        this.userService = new UserService(dataAccess);
        this.clearService = new ClearService(dataAccess);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
                .exception(DataAccessException.class, this::exceptionHandler);
    }

    public Server(UserService userService, ClearService clearService) {
        this.userService = userService;
        this.clearService = clearService;

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", this::clear)
                .post("/user", this::register)
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
