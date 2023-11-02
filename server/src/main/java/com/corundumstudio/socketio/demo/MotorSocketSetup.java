=package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.listener.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import com.corundumstudio.socketio.*;

public class MotorSocketSetup {

    // A set to keep track of connected users
    public static Set<String> userQueue = new HashSet<>();

    // A semaphore to control the number of concurrent connections (1 connection at a time)
    private static Semaphore connectionSemaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {

        // Configure the Socket.IO server
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);

        // Event handler for checking user existence
        server.addEventListener("checkUser", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String userId, final AckRequest ackRequest) {
                // Check if the user exists in the set
                boolean userExists = userQueue.contains(userId);
                // Send the result back to the client
                client.sendEvent("checkExist", userExists);
                System.out.printf("User with id %s exists " + userId);
            }
        });

        // Connect listener to handle new client connections
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                // Try to acquire a connection permit
                if (connectionSemaphore.tryAcquire()) {
                    String userId = client.getSessionId().toString();
                    // Add the user to the set of connected users
                    userQueue.add(userId);
                    // Broadcast a message to all clients
                    server.getBroadcastOperations().sendEvent("newUserJoined", "New user joined with ID: " + userId);
                    System.out.println("New user joined with ID: " + userId);
                } else {
                    // If the permit is not available, refuse the connection by disconnecting the client
                    client.disconnect();
                }
            }
        });

        // Event handlers for motor control
        server.addEventListener("up", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go up
                System.out.println("going up");
                MotorFunctions.up();
            }
        });

        server.addEventListener("left", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go left
                System.out.println("going left");
                MotorFunctions.left();
            }
        });

        server.addEventListener("right", String.class, new DataListener<String>() {
            @Override
            public onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go right
                System.out.println("going right");
                MotorFunctions.right();
            }
        });

        server.addEventListener("down", String.class, new DataListener<String>() {
            @Override
            public onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go down
                System.out.println("going down");
                MotorFunctions.down();
            }
        });

        // Start the Socket.IO server
        server.start();

        // Keep the program running until interrupted
        Thread.sleep(Integer.MAX_VALUE);

        // Stop the server when done
        server.stop();
    }
}
