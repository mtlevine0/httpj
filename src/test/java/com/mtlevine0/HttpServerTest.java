package com.mtlevine0;

import com.mtlevine0.handler.RequestHandler;
import com.mtlevine0.handler.RequestRouter;
import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

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

        RequestRouter requestRouter = new RequestRouter(basePath);
        requestRouter.registerRoute("*", HttpMethod.GET, new CustomHttpRequestHandler());
        requestRouter.registerRoute("/matt", HttpMethod.GET, new MattHandler());

        serverSocket = new ServerSocket(port);
        while (true) {
            executor.execute(new RequestDispatcher(serverSocket.accept(), requestRouter));
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public class CustomHttpRequestHandler implements RequestHandler {
        @Override
        public HttpResponse handleRequest(HttpRequest httpRequest) {
            return HttpResponse.builder().status(HttpStatus.NOT_FOUND).body("NOT HERE!".getBytes()).build();
        }
    }

    public class MattHandler implements RequestHandler {
        @Override
        public HttpResponse handleRequest(HttpRequest httpRequest) {
            return HttpResponse.builder().status(HttpStatus.OK).body("Cool Beans...".getBytes()).build();
        }
    }

}