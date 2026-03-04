package dataaccess;

import model.UserData;
import model.AuthData;

public interface DataAccess {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;
}