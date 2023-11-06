package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.listener.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import com.corundumstudio.socketio.*;

public class MotorSocketSetup {

    // Create a set to keep track of connected users.
    public static Set<String> userQueue = new HashSet<>();

    // Create a semaphore to control the number of allowed connections.
    private static Semaphore connectionSemaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {

        // Configure the server's settings.
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        // Initialize the Socket.IO server.
        final SocketIOServer server = new SocketIOServer(config);

        // Event listener for checking if a user exists.
        server.addEventListener("checkUser", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String userId, final AckRequest ackRequest) {
                boolean userExists = userQueue.contains(userId);
                client.sendEvent("checkExist", userExists);
                System.out.printf("User with id %s exists", userId);
            }
        });

        // Event listener for when a client connects to the server.
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                // Attempt to acquire a permit from the semaphore to control connections.
                if (connectionSemaphore.tryAcquire()) {
                    String userId = client.getSessionId().toString();
                    userQueue.add(userId);
                    // Broadcast a message to inform other clients about the new user.
                    server.getBroadcastOperations().sendEvent("newUserJoined", "New user joined with ID: " + userId);
                    System.out.println("New user joined with ID: " + userId);
                } else {
                    // If the semaphore permit is not available, disconnect the client.
                    client.disconnect();
                }
            }
        });

        // Event listener for when a client disconnects from the server.
        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                // Release the semaphore permit when a user disconnects.
                connectionSemaphore.release();
                String userId = client.getSessionId().toString();
                userQueue.remove(userId);
                System.out.println("User disconnected with ID: " + userId);
            }
        });

        // Add event listeners for motor control commands (up, left, right, down).
        // These will perform specific actions and may call external functions.
        // ...

        // Start the Socket.IO server.
        server.start();

        // Keep the server running.
        Thread.sleep(Integer.MAX_VALUE);

        // Stop the server when no longer needed.
        server.stop();
    }
}
