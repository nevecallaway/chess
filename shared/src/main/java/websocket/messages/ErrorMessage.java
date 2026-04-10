package websocket.messages;

/**
 * Server message sent when a client sends an invalid command.
 * The error message must include the word "Error".
 */
public class ErrorMessage extends ServerMessage {
    private String errorMessage;

    public ErrorMessage(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
