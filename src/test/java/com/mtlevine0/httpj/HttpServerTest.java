package com.mtlevine0.httpj;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.router.BasicRouter;
import com.mtlevine0.router.Route;
import com.mtlevine0.router.Router;
import com.mtlevine0.httpj.common.CustomRequestHandler;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.response.HttpStatus;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Logger;

public class HttpServerTest {
    private static Logger LOGGER = Logger.getLogger(HttpServerTest.class.getName());
    private ServerSocket serverSocket;

    public static void main( String[] args ) throws IOException {
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.DIRECTORY_LISTING);
        FeatureFlagContext.getInstance().enableFeature(FeatureFlag.STATIC_FILE_SERVER);
        LOGGER.info("Starting httpj Server...");
        logFeatureFlags();

        Router router = new BasicRouter();
        router.registerRoute(new Route("/test", HttpMethod.GET, (req, res) -> {
            res.setStatus(HttpStatus.OK);
            res.setBody("testing123".getBytes());
        }));

        HttpJ httpJServer = new HttpJ(router);
        httpJServer.start(8080);
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

//    public void start(int port) throws IOException {
//        Executor executor = Executors.newFixedThreadPool(10);
//
//        Router router = new BasicRouter();
//        router.registerRoute(new Route("/info", HttpMethod.GET, new HttpServerTest.InfoHandler()));
//        router.registerRoute(new Route("/middleware", HttpMethod.GET,
//                new MiddlewareService.MiddlewareServiceHandler()));
//        serverSocket = new ServerSocket(port);
//        while (serverSocket.isBound()) {
//            executor.execute(new RequestDispatcher(serverSocket.accept(), router));
//        }
//    }

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