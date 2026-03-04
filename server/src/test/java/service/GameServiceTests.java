package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.request.CreateGameRequest;
import service.request.RegisterRequest;
import service.result.CreateGameResult;
import service.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
        userService = new UserService(dataAccess);
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "user1@test.com");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest request = new CreateGameRequest("My Game", registerResult.authToken());
        CreateGameResult result = gameService.createGame(request);

        assertNotNull(result, "null");
        assertTrue(result.gameID() > 0, "> 0");
    }

    @Test
    public void testCreateGameInvalidToken() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("My Game", "invalidToken123");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });

        assertTrue(exception.getMessage().contains("Auth token not found"));
    }
}
