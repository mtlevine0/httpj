package com.mtlevine0.httpj;

import com.mtlevine0.router.handlers.RequestHandler;
import com.mtlevine0.router.Router;
import com.mtlevine0.httpj.request.HttpMethod;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServerTest {
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
        String basePath = "../httpj";
        Executor executor = Executors.newFixedThreadPool(10);

        Router router = new Router(basePath);
        router.registerRoute("*", HttpMethod.GET, new CustomHttpRequestHandler());
        router.registerRoute("/matt", HttpMethod.GET, new MattHandler());

        serverSocket = new ServerSocket(port);
        while (true) {
            executor.execute(new RequestDispatcher(serverSocket.accept(), router));
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public class CustomHttpRequestHandler implements RequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
            httpResponse.setBody("NOT HERE!".getBytes());
            httpResponse.setStatus(HttpStatus.NOT_FOUND);
        }
    }

    public class MattHandler implements RequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
            httpResponse.setBody("Cool Beans...".getBytes());
            httpResponse.setStatus(HttpStatus.OK);
        }
    }

}