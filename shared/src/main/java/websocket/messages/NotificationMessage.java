package websocket.messages;

/**
 * Server message sent to inform players of game events.
 * Examples: a player connected, a move was made, a player left, etc.
 */
public class NotificationMessage extends ServerMessage {
    private String message;

    public NotificationMessage(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
