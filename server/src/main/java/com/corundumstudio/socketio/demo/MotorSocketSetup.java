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

        server.addListeners(new DataListener<String>("checkUser") {
            @Override
            protected void onData(SocketIOClient client, String userId, AckRequest ackRequest) {
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

        server.addListeners(new DataListener<String>("up") {
            @Override
            protected void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                System.out.println("going up");
                MotorFunctions.up();
            }
        });

        server.addListeners(new DataListener<String>("left") {
            @Override
            protected void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                System.out.println("going left");
                MotorFunctions.left();
            }
        });

        server.addListeners(new DataListener<String>("right") {
            @Override
            protected void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                System.out.println("going right");
                MotorFunctions.right();
            }
        });

        server.addListeners(new DataListener<String>("down") {
            @Override
            protected void onData(SocketIOClient client, String data, AckRequest ackRequest) {
                System.out.println("going down");
                MotorFunctions.down();
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
