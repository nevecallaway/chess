package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;
import service.request.RegisterRequest;
import service.request.LoginRequest;
import service.result.RegisterResult;
import service.result.LoginResult;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        try {
            dataAccess.getUser(request.username());
            throw new DataAccessException("User already exists");
        } catch (DataAccessException e) {
            if (e.getMessage().contains("already exists")) {
                throw e;
            }
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(user);

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);

        return new RegisterResult(request.username(), authToken);
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        UserData user = dataAccess.getUser(request.username());

        if (!user.password().equals(request.password())) {
            throw new DataAccessException("Invalid password");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, request.username());
        dataAccess.createAuth(auth);

        return new LoginResult(request.username(), authToken);
    }
}