package client;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.CloseReason;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;


/**
 * WebSocket endpoint implementation for connecting to the Chess server.
 * Handles message receiving and sending via WebSocket.
 */
public class WebSocketClientEndpoint {
    private Object userSession;
    private WebSocketManager manager;
    private CountDownLatch latch = new CountDownLatch(1);

    public WebSocketClientEndpoint(WebSocketManager manager) {
        this.manager = manager;
    }

    public void connectToServer(URI serverUri) throws Exception {
        ClientManager client = ClientManager.createClient();
        
        try {
            // Create and connect using Tyrus
            userSession = client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    // Store the session and add message handler
                    WebSocketClientEndpoint.this.userSession = session;
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                manager.handleMessage(message);
                            }
                        });
                        latch.countDown();
                    } catch (Exception e) {
                        System.err.println("Error setting up message handler: " + e.getMessage());
                    }
                }
                
                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    WebSocketClientEndpoint.this.userSession = null;
                }
                
                @Override
                public void onError(Session session, Throwable thr) {
                    System.err.println("WebSocket error: " + thr.getMessage());
                    thr.printStackTrace();
                }
            }, serverUri);
            
            // Wait for the endpoint to be ready
            latch.await();
        } catch (Exception e) {
            throw new Exception("Failed to connect to WebSocket: " + e.getMessage(), e);
        }
    }

    public void sendMessage(String message) throws IOException {
        if (userSession != null) {
            try {
                Object session = userSession;
                if (session instanceof Session) {
                    ((Session) session).getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                throw e;
            }
        } else {
            throw new IOException("Session not connected");
        }
    }

    public boolean isOpen() {
        if (userSession != null && userSession instanceof Session) {
            return ((Session) userSession).isOpen();
        }
        return false;
    }

    public void close() throws IOException {
        if (userSession != null && userSession instanceof Session) {
            ((Session) userSession).close();
        }
    }
}
