package dataaccess;

import chess.ChessGame;
import com.google.gson.*;
import java.lang.reflect.Type;

public class ChessGameAdapter implements JsonSerializer<ChessGame>, JsonDeserializer<ChessGame> {

    @Override
    public JsonElement serialize(ChessGame src, Type typeOfSrc, JsonSerializationContext context) {
        // Create a wrapper with just the team turn since board is complex
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("teamTurn", src.getTeamTurn().toString());
        } catch (Exception e) {
            obj.addProperty("teamTurn", "WHITE");
        }
        return obj;
    }

    @Override
    public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Always create a fresh game
        ChessGame game = new ChessGame();
        
        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("teamTurn")) {
                String teamTurn = obj.get("teamTurn").getAsString();
                game.setTeamTurn(ChessGame.TeamColor.valueOf(teamTurn));
            }
        } catch (Exception e) {
            // Ignore - just return default game
        }
        
        return game;
    }
}
