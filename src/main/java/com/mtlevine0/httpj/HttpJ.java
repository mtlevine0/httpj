package com.mtlevine0.httpj;

import com.mtlevine0.httpj.common.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpJ {
    private RequestHandler requestHandler;
    private  ServerSocket serverSocket;
    private ExecutorService executor;

    public HttpJ(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void start(int port) {
        executor = Executors.newFixedThreadPool(10);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.serverSocket = serverSocket;
            while (serverSocket.isBound()) {
                executor.execute(new RequestDispatcher(serverSocket.accept(), requestHandler));
            }
        } catch (Exception e) {
            this.stop();
        }
    }

    public void stop() {
        this.executor.shutdownNow();
        try {
            this.serverSocket.close();
        } catch (IOException e) {

        }
    }
}
