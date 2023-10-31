package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.listener.*;

import java.util.HashSet;
import java.util.Set;

import com.corundumstudio.socketio.*;


public class MotorSocketSetup {

        public static Set<String> userQueue = new HashSet<>();

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);

        // probably have to change `String.class` to `Integer.class` I'd check if ID is a integer or stinrg
        server.addEventListener("checkUser", String.class, new DataListener<String>(){
            @Override
            public void onData(final SocketIOClient client, String userId, final AckRequest ackRequest){
                boolean userExists = userQueue.contains(userId);
                client.sendEvent("checkExist", userExists);
                System.out.printf("User with id %s exists "+userId);

            }
        });


        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                String userId = client.getSessionId().toString();
                userQueue.add(userId);
                server.getBroadcastOperations().sendEvent("newUserJoined", "New user joined with ID: "+userId);
                System.out.println("New user joined with ID: " + userId);
            }
            
        });
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
                // run code to go left
                System.out.println("going left");
                MotorFunctions.left();
            }
        });

        server.addEventListener("right", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // run code to go right
                System.out.println("going right");
                MotorFunctions.right();
            }
        });


        server.addEventListener("down", String.class, new DataListener<String>() {
            @Override
            public void onData(final SocketIOClient client, String data, final AckRequest ackRequest) {
                // run code to go down
                System.out.println("going down");
                MotorFunctions.down();
            }
        });



        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
