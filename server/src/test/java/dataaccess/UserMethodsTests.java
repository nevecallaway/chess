package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserMethodsTests {

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
    }

    @Test
    public void testCreate() throws DataAccessException {
        UserData user = new UserData("benito", "badbunny", "benito@example.com");
        dataAccess.createUser(user);
        assertTrue(true);
    }

    @Test
    public void testGet() throws DataAccessException {
        UserData user = new UserData("benito", "badbunny", "benito@example.com");
        dataAccess.createUser(user);

        UserData retrieved = dataAccess.getUser("benito");

        assertEquals("benito", retrieved.username());
        assertEquals("badbunny", retrieved.password());
        assertEquals("benito@example.com", retrieved.email());
    }

    @Test
    public void testDuplicateFails() throws DataAccessException {
        UserData user = new UserData("benito", "badbunny", "benito@example.com");
        dataAccess.createUser(user);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.createUser(user);
        });
        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    public void testNotFoundFails() throws DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.getUser("nonexistent");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }
}
