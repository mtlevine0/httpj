package com.mtlevine0;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServer {
    private static Logger LOGGER = Logger.getLogger(HttpServer.class.getName());
    private ServerSocket serverSocket;

    public static void main( String[] args ) throws IOException {
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.DIRECTORY_LISTING);
        FeatureFlagContext.getInstance().disableFeature(FeatureFlag.SANITIZE_PATH); // disable directory traversal protection
        LOGGER.info("Starting httpj Server...");
        logFeatureFlags();
        HttpServer server = new HttpServer();
        server.start(8080);
    }

    private static void logFeatureFlags() {
        Map<FeatureFlag, Boolean> flags = FeatureFlagContext.getInstance().getFeatures();
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (FeatureFlag flag : flags.keySet()) {
            sb.append("Feature: " + flag.name() + " - " + flags.get(flag).toString() + "\n");
        }
        LOGGER.info(sb.toString());
    }

    public void start(int port) throws IOException {
        Executor executor = Executors.newFixedThreadPool(10);
        serverSocket = new ServerSocket(port);
        while (true) {
            executor.execute(new RequestDispatcher(serverSocket.accept(), "../httpj"));
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

}