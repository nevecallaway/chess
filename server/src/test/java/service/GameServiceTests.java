package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.request.CreateGameRequest;
import service.request.ListGamesRequest;
import service.request.JoinGameRequest;
import service.request.RegisterRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;
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
        RegisterRequest registerRequest = new RegisterRequest("benito", "password64", "benito@test.com");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest request = new CreateGameRequest("My Game", registerResult.authToken());
        CreateGameResult result = gameService.createGame(request);

        assertNotNull(result, "null");
        assertTrue(result.gameID() > 0, "> 0");
    }

    @Test
    public void testCreateGameInvalidToken() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("My Game", "invalidToken64");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });

        assertTrue(exception.getMessage().contains("Auth token not found"));
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("benito", "password64", "benito@test.com");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createRequest = new CreateGameRequest("Game 1", registerResult.authToken());
        gameService.createGame(createRequest);

        ListGamesRequest listRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult result = gameService.listGames(listRequest);

        assertNotNull(result, "null");
        assertEquals(1, result.games().size());
    }

    @Test
    public void testListGamesInvalidToken() throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest("invalidToken64");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.listGames(request);
        });

        assertTrue(exception.getMessage().contains("Auth token not found"));
    }

    @Test
    public void testJoinGamePositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("benito", "password64", "benito@test.com");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createRequest = new CreateGameRequest("My Game", registerResult.authToken());
        CreateGameResult createResult = gameService.createGame(createRequest);

        JoinGameRequest joinRequest = new JoinGameRequest(registerResult.authToken(), "WHITE", createResult.gameID());
        gameService.joinGame(joinRequest);

        // Should not throw
        assertTrue(true);
    }

    @Test
    public void testJoinGameInvalidToken() throws DataAccessException {
        JoinGameRequest request = new JoinGameRequest("invalidToken64", "WHITE", 1);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.joinGame(request);
        });

        assertTrue(exception.getMessage().contains("Auth token not found"));
    }
}
