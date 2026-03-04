package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import service.request.RegisterRequest;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.result.RegisterResult;
import service.result.LoginResult;
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

        assertNotNull(result, "null");
        assertEquals("benito", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());
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

        assertTrue(exception.getMessage().contains("already exists"));
    }

    // Login positive test

    @Test
    public void testLoginPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("benito", "password64", "benito@example.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("benito", "password64");
        LoginResult result = userService.login(loginRequest);

        assertNotNull(result, "null");
        assertEquals("benito", result.username());
        assertNotNull(result.authToken());
        assertFalse(result.authToken().isEmpty());
    }

    // Login negative test

    @Test
    public void testLoginWrongPassword() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("benito", "password64", "benito@example.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("benito", "wrongPassword");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.login(loginRequest);
        });

        assertTrue(exception.getMessage().contains("Invalid password"));
    }

    // Logout positive test

    @Test
    public void testLogoutPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("benito", "password64", "benito@example.com");
        RegisterResult registerResult = userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        userService.logout(logoutRequest);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.logout(logoutRequest);
        });

        assertTrue(exception.getMessage().contains("Auth token not found"));
    }

    // Logout negative test

    @Test
    public void testLogoutInvalidToken() throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest("invalidToken64");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userService.logout(logoutRequest);
        });

        assertTrue(exception.getMessage().contains("Auth token not found"));
    }
}
