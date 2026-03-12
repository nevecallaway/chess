package dataaccess;

import model.UserData;
import model.AuthData;
import model.GameData;
import java.sql.SQLException;
import java.util.List;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        initializeTables();
    }

    private void initializeTables() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            // Create users table
            var createUsersTable = """
                    CREATE TABLE IF NOT EXISTS users (
                        username VARCHAR(255) NOT NULL PRIMARY KEY,
                        password VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL
                    )""";
            try (var stmt = conn.prepareStatement(createUsersTable)) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to initialize users table", ex);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var sql = "DELETE FROM users";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to clear users", ex);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.username());
                stmt.setString(2, user.password());
                stmt.setString(3, user.email());
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("User already exists");
            }
            throw new DataAccessException("Failed to create user", ex);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var sql = "SELECT username, password, email FROM users WHERE username = ?";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("email")
                        );
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to get user", ex);
        }
        throw new DataAccessException("User not found");
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }

    @Override
    public int getNextGameId() {
        // TODO: Implement
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        // TODO: Implement
        throw new DataAccessException("Not yet implemented");
    }
}
