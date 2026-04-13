package client;

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * WebSocket endpoint implementation for connecting to the Chess server.
 * Handles message receiving and sending via WebSocket.
 */
public class WebSocketClientEndpoint extends Endpoint {
    private Session userSession;
    private WebSocketManager manager;
    private CountDownLatch latch = new CountDownLatch(1);
    private ScheduledExecutorService keepAliveExecutor;

    public WebSocketClientEndpoint(WebSocketManager manager) {
        this.manager = manager;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.userSession = session;
        // Start keep-alive heartbeat to prevent connection idle timeout
        startKeepAlive();
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        this.userSession = null;
        stopKeepAlive();
    }

    @Override
    public void onError(Session session, Throwable thr) {
        System.err.println("WebSocket error: " + thr.getMessage());
    }

    public void connectToServer(URI serverUri) throws Exception {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.userSession = container.connectToServer(this, serverUri);
            
            // Set message handler
            this.userSession.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    if (manager != null) {
                        manager.handleMessage(message);
                    }
                }
            });
            
            latch.countDown();
        } catch (DeploymentException | IOException ex) {
            throw new Exception("Failed to connect to WebSocket: " + ex.getMessage(), ex);
        }
    }

    public void sendMessage(String message) throws IOException {
        if (userSession != null && userSession.isOpen()) {
            userSession.getBasicRemote().sendText(message);
        } else {
            throw new IOException("Session not connected");
        }
    }

    public boolean isOpen() {
        return userSession != null && userSession.isOpen();
    }

    public void close() throws IOException {
        if (userSession != null) {
            userSession.close();
        }
    }

    /**
     * Start sending periodic ping frames to keep the connection alive.
     * Prevents server-side idle timeout by maintaining activity.
     */
    private void startKeepAlive() {
        keepAliveExecutor = Executors.newScheduledThreadPool(1);
        keepAliveExecutor.scheduleAtFixedRate(() -> {
            try {
                if (userSession != null && userSession.isOpen()) {
                    userSession.getBasicRemote().sendPing(ByteBuffer.wrap(new byte[]{}));
                }
            } catch (IOException e) {
                // Connection may have closed, that's okay
            }
        }, 25, 25, TimeUnit.SECONDS);
    }

    /**
     * Stop sending keep-alive pings.
     */
    private void stopKeepAlive() {
        if (keepAliveExecutor != null) {
            keepAliveExecutor.shutdown();
            try {
                if (!keepAliveExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    keepAliveExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                keepAliveExecutor.shutdownNow();
            }
        }
    }
}
