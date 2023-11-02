package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.listener.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import com.corundumstudio.socketio.*;

public class MotorSocketSetup {

    public static Set<String> userQueue = new HashSet<>();
    private static Semaphore connectionSemaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("checkUser", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String userId, final AckRequest ackRequest) {
                boolean userExists = userQueue.contains(userId);
                client.sendEvent("checkExist", userExists);
                System.out.printf("User with id %s exists " + userId);
            }
        });

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                if (connectionSemaphore.tryAcquire()) {
                    String userId = client.getSessionId().toString();
                    userQueue.add(userId);
                    server.getBroadcastOperations().sendEvent("newUserJoined", "New user joined with ID: " + userId);
                    System.out.println("New user joined with ID: " + userId);
                } else {
                    client.disconnect();
                }
            }
        });

        server.addEventListener("up", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go up
                System.out.println("going up");
                // Call a motor control function to move up
                MotorFunctions.up();
            }
        });

        server.addEventListener("left", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go left
                System.out.println("going left");
                // Call a motor control function to move left
                MotorFunctions.left();
            }
        });

        server.addEventListener("right", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go right
                System.out.println("going right");
                // Call a motor control function to move right
                MotorFunctions.right();
            }
        });

        server.addEventListener("down", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // Run code to go down
                System.out.println("going down");
                // Call a motor control function to move down
                MotorFunctions.down();
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

    // Placeholder for motor control functions (replace with actual implementation)
    public static class MotorFunctions {
        public static void up() {
            // Implement the logic to move the motors up
        }

        public static void left() {
            // Implement the logic to move the motors left
        }

        public static void right() {
            // Implement the logic to move the motors right
        }

        public static void down() {
            // Implement the logic to move the motors down
        }
    }
}
