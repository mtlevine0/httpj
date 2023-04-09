package com.mtlevine0.httpj;

import com.mtlevine0.httpj.request.HttpMethod;
import com.mtlevine0.router.Route;
import com.mtlevine0.router.handlers.CustomRequestHandler;
import com.mtlevine0.router.Router;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;
import com.mtlevine0.router.middleware.MiddlewareService;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class HttpServerTest {
    private static Logger LOGGER = Logger.getLogger(HttpServerTest.class.getName());
    private ServerSocket serverSocket;

    public static void main( String[] args ) throws IOException {
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.DIRECTORY_LISTING);
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.STATIC_FILE_SERVER);
        LOGGER.info("Starting httpj Server...");
        logFeatureFlags();
        HttpServerTest server = new HttpServerTest();
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

        Router router = new Router();
        router.registerRoute(new Route("/info", HttpMethod.GET, new HttpServerTest.InfoHandler()));
        router.registerRoute(new Route("/middleware", HttpMethod.GET,
                new MiddlewareService.MiddlewareServiceHandler()));
        serverSocket = new ServerSocket(port);
        while (serverSocket.isBound()) {
            executor.execute(new RequestDispatcher(serverSocket.accept(), router));
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public class CustomHandler implements CustomRequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
            httpResponse.setBody("Custom!".getBytes());
            httpResponse.setStatus(HttpStatus.OK);
        }
    }

    public class InfoHandler implements CustomRequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
            StringBuilder sb = new StringBuilder();
            sb.append(Instant.now().toString() + "\n");
            sb.append("Custom Request Handler!" + "\n\n");
            sb.append("Request Headers:\n");
            for (String key : httpRequest.getHeaders().keySet()) {
                sb.append(key + ": " + httpRequest.getHeaders().get(key) + "\n");
            }
            sb.append("Query Params:\n");
            for (String key : httpRequest.getQueryParams().keySet()) {
                sb.append(key + ": " + httpRequest.getQueryParams().get(key) + "\n");
            }
            httpResponse.setBody(sb.toString().getBytes());
            httpResponse.setStatus(HttpStatus.OK);
        }
    }

}