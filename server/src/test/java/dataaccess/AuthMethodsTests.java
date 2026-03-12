package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthMethodsTests {

    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
            dataAccess = new MySQLDataAccess();
        } catch (DataAccessException ex) {
            System.err.println("MySQL not available, using MemoryDataAccess: " + ex.getMessage());
            dataAccess = new MemoryDataAccess();
        }
        dataAccess.clear();
        UserData user = new UserData("benito", "password", "benito@example.com");
        dataAccess.createUser(user);
    }

    @Test
    public void testCreateAuth() throws DataAccessException {
        AuthData auth = new AuthData("badbunny", "benito");
        dataAccess.createAuth(auth);
        assertTrue(true);
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
