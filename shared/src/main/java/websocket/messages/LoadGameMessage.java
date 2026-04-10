package websocket.messages;

/**
 * Server message that sends the current game state to a client.
 * Sent when a client connects or when a move is made.
 */
public class LoadGameMessage extends ServerMessage {
    private Object game;

    public LoadGameMessage(Object game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public Object getGame() {
        return game;
    }

    public void setGame(Object game) {
        this.game = game;
    }
}
