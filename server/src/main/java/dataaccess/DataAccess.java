package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;
import java.util.List;

public interface DataAccess {
    void clear() throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    int getNextGameId();

    void createGame(GameData game) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;
}