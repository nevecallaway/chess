package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMethodsTests {

    private DataAccess dataAccess;

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Try to use MySQL, fall back to memory if it fails
        try {
            DatabaseManager.createDatabase();
            dataAccess = new MySQLDataAccess();
        } catch (DataAccessException ex) {
            System.err.println("MySQL not available, using MemoryDataAccess: " + ex.getMessage());
            ex.printStackTrace();
            dataAccess = new MemoryDataAccess();
        }
        dataAccess.clear();
    }

    @Test
    public void testCreateUser() throws DataAccessException {
        UserData user = new UserData("benito", "badbunny", "benito@example.com");
        dataAccess.createUser(user);

        // If here without an exception, then test passed
        assertTrue(true, "User created successfully");
    }

    @Test
    public void testGetUser() throws DataAccessException {
        UserData user = new UserData("benito", "badbunny", "benito@example.com");
        dataAccess.createUser(user);

        UserData retrieved = dataAccess.getUser("benito");

        assertEquals("benito", retrieved.username());
        assertEquals("badbunny", retrieved.password());
        assertEquals("benito@example.com", retrieved.email());
    }

    @Test
    public void testCreateDuplicateUserFails() throws DataAccessException {
        UserData user = new UserData("benito", "badbunny", "benito@example.com");
        dataAccess.createUser(user);

        // Try to create duplicate
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user);
        });
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void testGetNonexistentUserFails() throws DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.getUser("nonexistent");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }
}
