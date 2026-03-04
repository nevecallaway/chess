package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.request.CreateGameRequest;
import service.result.CreateGameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameService gameService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("My Game");
        CreateGameResult result = gameService.createGame(request);

        assertNotNull(result, "null");
        assertTrue(result.gameID() > 0, "> 0");
    }

    @Test
    public void testCreateGameEmptyName() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            gameService.createGame(request);
        });

        System.out.println("Exception message: '" + exception.getMessage() + "'");
        assertTrue(exception.getMessage().contains("Game name can't be empty"));
    }
}
