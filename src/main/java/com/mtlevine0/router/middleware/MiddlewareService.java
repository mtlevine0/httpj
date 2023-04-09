package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;
import com.mtlevine0.router.handlers.CustomRequestHandler;
import com.mtlevine0.router.Router;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiddlewareService {

    private static List<Middleware> middlewares;
    private int routerIndex = 0;

    public MiddlewareService(Router router) {
        middlewares = new ArrayList<>();

        registerPreRouter(new LoggingMiddleware());
        registerPostRouter(new HelloMiddleware());
        if(FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.GZIP_ENCODING)) {
            registerPostRouter(new GzipMiddleware());
        }
    }

    public void registerPreRouter(Middleware middleware) {
        this.middlewares.add(++routerIndex - 1, middleware);
    }

    public void registerPostRouter(Middleware middleware) {
        this.middlewares.add(middleware);
    }

    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        Exception middlewareException = null;
        for(Middleware middleware: middlewares) {
            try {
                middleware.handleRequest(request, response);
            } catch (Exception e) {
                middlewareException = e;
            }
        }
        if (Objects.nonNull(middlewareException)) {
            throw middlewareException;
        }
    }

    public List<Middleware> getMiddlewares() {
        return this.middlewares;
    }

    public static class MiddlewareServiceHandler implements CustomRequestHandler {
        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse response) {
            StringBuilder sb = new StringBuilder();
            for(Middleware middleware: middlewares) {
                sb.append(middleware.getClass().getName() + "\n");
            }
            response.setBody(sb.toString().getBytes());
            response.setStatus(HttpStatus.OK);
        }
    }
}
