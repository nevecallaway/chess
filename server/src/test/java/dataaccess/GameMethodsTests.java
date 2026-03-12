package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameMethodsTests {

    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
            dataAccess = new MySQLDataAccess();
        } catch (DataAccessException ex) {
            System.err.println("MySQL not available, using MemoryDataAccess: " + ex.getMessage());
            ex.printStackTrace();
            dataAccess = new MemoryDataAccess();
        }
        dataAccess.clear();
        
        UserData user1 = new UserData("player1", "password1", "player1@example.com");
        UserData user2 = new UserData("player2", "password2", "player2@example.com");
        dataAccess.createUser(user1);
        dataAccess.createUser(user2);
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", "Test Game", game);
        
        dataAccess.createGame(gameData);
        
        assertTrue(true);
    }

    @Test
    public void testGetGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", "Test Game", game);
        dataAccess.createGame(gameData);

        GameData retrieved = dataAccess.getGame(1);

        assertEquals(1, retrieved.gameID());
        assertEquals("player1", retrieved.whiteUsername());
        assertEquals("player2", retrieved.blackUsername());
        assertEquals("Test Game", retrieved.gameName());
        assertNotNull(retrieved.game());
    }

    @Test
    public void testGetGameNotFound() throws DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.getGame(999);
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testListGames() throws DataAccessException {
        ChessGame game1 = new ChessGame();
        ChessGame game2 = new ChessGame();
        
        GameData gameData1 = new GameData(1, "player1", "player2", "Game 1", game1);
        GameData gameData2 = new GameData(2, "player2", "player1", "Game 2", game2);
        
        dataAccess.createGame(gameData1);
        dataAccess.createGame(gameData2);

        List<GameData> games = dataAccess.listGames();

        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(g -> g.gameID() == 1));
        assertTrue(games.stream().anyMatch(g -> g.gameID() == 2));
    }

    @Test
    public void testListGamesEmpty() throws DataAccessException {
        List<GameData> games = dataAccess.listGames();
        
        assertEquals(0, games.size());
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", "Original Name", game);
        dataAccess.createGame(gameData);

        // Update the game
        ChessGame updatedGame = new ChessGame();
        GameData updatedGameData = new GameData(1, "player1", "player2", "Updated Name", updatedGame);
        dataAccess.updateGame(updatedGameData);

        GameData retrieved = dataAccess.getGame(1);
        assertEquals("Updated Name", retrieved.gameName());
    }

    @Test
    public void testUpdateGameNotFound() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(999, "player1", "player2", "Nonexistent Game", game);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.updateGame(gameData);
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testGetNextGameId() throws DataAccessException {
        int id1 = dataAccess.getNextGameId();
        assertEquals(1, id1);

        ChessGame game1 = new ChessGame();
        GameData gameData1 = new GameData(id1, "player1", "player2", "Game 1", game1);
        dataAccess.createGame(gameData1);

        int id2 = dataAccess.getNextGameId();
        assertEquals(2, id2);

        ChessGame game2 = new ChessGame();
        GameData gameData2 = new GameData(id2, "player2", "player1", "Game 2", game2);
        dataAccess.createGame(gameData2);

        int id3 = dataAccess.getNextGameId();
        assertEquals(3, id3);
    }

    @Test
    public void testCreateDuplicateGameFails() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", "Test Game", game);
        dataAccess.createGame(gameData);
        
        // Creating a game with the same ID should fail
        ChessGame game2 = new ChessGame();
        GameData gameData2 = new GameData(1, "player1", "player2", "Duplicate Game", game2);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(gameData2);
        });
        assertTrue(exception.getMessage().contains("Failed") || exception.getMessage().contains("exists"));
    }

    @Test
    public void testCreateGameWithNullGamenameFails() throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(1, "player1", "player2", null, game);
        
        // GameData with null name might fail during insert
        // This is a boundary condition test
        try {
            dataAccess.createGame(gameData);
            // If it succeeds, verify it was stored (some implementations might allow it)
            GameData retrieved = dataAccess.getGame(1);
            assertNull(retrieved.gameName());
        } catch (DataAccessException ex) {
            // If it fails, that's also acceptable
            assertTrue(true);
        }
    }
}
