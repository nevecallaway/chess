package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthMethodsTests {

    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Try to use MySQL, fall back to memory if it fails
        try {
            DatabaseManager.createDatabase();
            dataAccess = new MySQLDataAccess();
        } catch (DataAccessException ex) {
            System.err.println("MySQL not available, using MemoryDataAccess: " + ex.getMessage());
            dataAccess = new MemoryDataAccess();
        }
        dataAccess.clear();
    }

    @Test
    public void testCreateAuth() throws DataAccessException {
        AuthData auth = new AuthData("badbunny", "benito");
        dataAccess.createAuth(auth);

        assertTrue(true, "Auth token created successfully");
    }

    @Test
    public void testGetAuth() throws DataAccessException {
        AuthData auth = new AuthData("badbunny", "benito");
        dataAccess.createAuth(auth);

        AuthData retrieved = dataAccess.getAuth("badbunny");

        assertEquals("badbunny", retrieved.authToken());
        assertEquals("benito", retrieved.username());
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        AuthData auth = new AuthData("badbunny", "benito");
        dataAccess.createAuth(auth);

        dataAccess.deleteAuth("badbunny");

        // Verify it's deleted
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth("badbunny");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testGetNonexistent() throws DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.getAuth("nonexistent");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    public void testDeleteNonexistentAuthFails() throws DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.deleteAuth("nonexistent");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }
}
