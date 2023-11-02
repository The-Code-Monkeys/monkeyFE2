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

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }
}
