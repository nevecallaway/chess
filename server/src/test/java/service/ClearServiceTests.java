package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.request.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    private ClearService clearService;
    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);
    }

    @Test
    public void testClearPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("benito", "password64", "benito@example.com");
        userService.register(request);

        dataAccess.getUser("benito");

        clearService.clear();

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            dataAccess.getUser("benito");
        });

        assertTrue(exception.getMessage().contains("not found"), 
                "User should not exist after clear");
    }
}
