package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import service.request.CreateGameRequest;
import service.request.ListGamesRequest;
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
}
