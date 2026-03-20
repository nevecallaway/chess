package client;

import org.junit.jupiter.api.*;
import server.Server;
import service.result.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws Exception {
        facade.clear();
    }

    @Test
    void registerPositive() throws Exception {
        RegisterResult result = facade.register("player1", "password", "player1@email.com");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("player1", result.username());
        Assertions.assertTrue(result.authToken().length() > 10);
    }

    @Test
    void registerNegativeDuplicateUsername() throws Exception {
        facade.register("player1", "password", "player1@email.com");
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.register("player1", "password2", "player1b@email.com");
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void loginPositive() throws Exception {
        facade.register("player1", "password", "player1@email.com");
        LoginResult result = facade.login("player1", "password");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("player1", result.username());
        Assertions.assertTrue(result.authToken().length() > 10);
    }

    @Test
    void loginNegativeWrongPassword() throws Exception {
        facade.register("player1", "password", "player1@email.com");
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.login("player1", "wrongpassword");
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void logoutPositive() throws Exception {
        RegisterResult registerResult = facade.register("player1", "password", "player1@email.com");
        facade.logout(registerResult.authToken());
        Assertions.assertTrue(true);
    }

    @Test
    void logoutNegativeInvalidToken() throws Exception {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.logout("invalidtoken");
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void createGamePositive() throws Exception {
        RegisterResult registerResult = facade.register("player1", "password", "player1@email.com");
        CreateGameResult result = facade.createGame("My Game", registerResult.authToken());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameNegativeUnauthorized() throws Exception {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.createGame("My Game", "invalidtoken");
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void listGamesPositive() throws Exception {
        RegisterResult registerResult = facade.register("player1", "password", "player1@email.com");
        ListGamesResult result = facade.listGames(registerResult.authToken());
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.games());
        Assertions.assertEquals(0, result.games().size());
    }

    @Test
    void listGamesWithGames() throws Exception {
        RegisterResult registerResult = facade.register("player1", "password", "player1@email.com");
        facade.createGame("Game1", registerResult.authToken());
        facade.createGame("Game2", registerResult.authToken());
        ListGamesResult result = facade.listGames(registerResult.authToken());
        Assertions.assertEquals(2, result.games().size());
    }

    @Test
    void listGamesNegativeUnauthorized() throws Exception {
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.listGames("invalidtoken");
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void joinGamePositiveAsWhite() throws Exception {
        RegisterResult registerResult = facade.register("player1", "password", "player1@email.com");
        CreateGameResult createResult = facade.createGame("My Game", registerResult.authToken());
        facade.joinGame(registerResult.authToken(), "WHITE", createResult.gameID());
        Assertions.assertTrue(true);
    }

    @Test
    void joinGamePositiveAsBlack() throws Exception {
        RegisterResult player1 = facade.register("player1", "password", "player1@email.com");
        RegisterResult player2 = facade.register("player2", "password", "player2@email.com");
        CreateGameResult createResult = facade.createGame("My Game", player1.authToken());
        facade.joinGame(player1.authToken(), "WHITE", createResult.gameID());
        facade.joinGame(player2.authToken(), "BLACK", createResult.gameID());
        Assertions.assertTrue(true);
    }

    @Test
    void joinGameNegativeUnauthorized() throws Exception {
        RegisterResult registerResult = facade.register("player1", "password", "player1@email.com");
        CreateGameResult createResult = facade.createGame("My Game", registerResult.authToken());
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame("invalidtoken", "WHITE", createResult.gameID());
        });
        Assertions.assertNotNull(exception);
    }

    @Test
    void joinGameNegativeColorTaken() throws Exception {
        RegisterResult player1 = facade.register("player1", "password", "player1@email.com");
        RegisterResult player2 = facade.register("player2", "password", "player2@email.com");
        CreateGameResult createResult = facade.createGame("My Game", player1.authToken());
        facade.joinGame(player1.authToken(), "WHITE", createResult.gameID());
        Exception exception = Assertions.assertThrows(Exception.class, () -> {
            facade.joinGame(player2.authToken(), "WHITE", createResult.gameID());
        });
        Assertions.assertNotNull(exception);
    }

}
