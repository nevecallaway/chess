package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import service.request.CreateGameRequest;
import service.result.CreateGameResult;
import chess.ChessGame;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new DataAccessException("Game name can't be empty");
        }

        int gameID = dataAccess.getNextGameId();
        GameData game = new GameData(gameID, null, null, request.gameName(), new ChessGame());
        dataAccess.createGame(game);

        return new CreateGameResult(gameID);
    }
}
