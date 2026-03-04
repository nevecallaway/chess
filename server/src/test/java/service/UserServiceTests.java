package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.request.RegisterRequest;
import service.result.RegisterResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    // Register positive test

    @Test
    public void testRegisterPositive() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("benito", "password64", "benito@example.com");
        RegisterResult result = userService.register(request);

        assertNotNull(result, "Result shouldn't be null");
        assertEquals("benito", result.username(), "Username should match");
        assertNotNull(result.authToken(), "Auth token should be generated");
        assertFalse(result.authToken().isEmpty(), "Auth token shouldn't be empty");
    }

    // Register negative

    @Test
    public void testRegisterDuplicate() throws DataAccessException {
        RegisterRequest request1 = new RegisterRequest("benito", "password64", "benito@example.com");
        userService.register(request1);

        RegisterRequest request2 = new RegisterRequest("benito", "differentPassword", "benitodifferent@example.com");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.register(request2);
        });

        assertTrue(exception.getMessage().contains("already exists"), 
                "Exception message should say user already exists");
    }
}
