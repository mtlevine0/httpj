package com.mtlevine0;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {
    private static Logger LOGGER = Logger.getLogger(HttpServer.class.getName());
    private ServerSocket serverSocket;

    public static void main( String[] args ) throws IOException {
        LOGGER.info("Starting httpj Server...");
        HttpServer server = new HttpServer();
        server.start(8080);
    }

    public void start(int port) throws IOException {
        Executor executor = Executors.newFixedThreadPool(10);
        serverSocket = new ServerSocket(port);
        while (true) {
            executor.execute(new HttpThread(serverSocket.accept()));
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

}