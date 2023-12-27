package com.mtlevine0.httpj;

import com.mtlevine0.httpj.common.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpJ {
    private ServerSocket serverSocket;
    private RequestHandler requestHandler;

    public HttpJ(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void start(int port) throws IOException {
        Executor executor = Executors.newFixedThreadPool(10);
        serverSocket = new ServerSocket(port);
        while (serverSocket.isBound()) {
            executor.execute(new RequestDispatcher(serverSocket.accept(), requestHandler));
        }
    }
}
