package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import service.request.CreateGameRequest;
import service.request.ListGamesRequest;
import service.request.JoinGameRequest;
import service.result.CreateGameResult;
import service.result.ListGamesResult;
import chess.ChessGame;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        dataAccess.getAuth(request.authToken());

        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("Game name can't be empty");
        }

        int gameID = dataAccess.getNextGameId();
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        dataAccess.createGame(game);

        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(ListGamesRequest request) throws DataAccessException {
        dataAccess.getAuth(request.authToken());
        return new ListGamesResult(dataAccess.listGames());
    }

    public void joinGame(JoinGameRequest request) throws DataAccessException {
        AuthData auth = dataAccess.getAuth(request.authToken());
        String username = auth.username();

        if (request.playerColor() == null || request.playerColor().isEmpty()) {
            throw new DataAccessException("Player color is required");
        }

        GameData game = dataAccess.getGame(request.gameID());

        if (request.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("White player already taken");
            }
            game = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        } else if (request.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Black player already taken");
            }
            game = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        } else {
            throw new DataAccessException("Invalid player color");
        }

        dataAccess.updateGame(game);
    }
}
