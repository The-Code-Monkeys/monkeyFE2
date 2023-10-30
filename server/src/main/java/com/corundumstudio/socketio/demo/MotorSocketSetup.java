package com.corundumstudio.socketio.demo;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;


public class MotorSocketSetup {

    public static void main(String[] args) throws InterruptedException {

        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
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
