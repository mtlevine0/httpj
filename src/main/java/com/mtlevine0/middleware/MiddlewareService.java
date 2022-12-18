package com.mtlevine0.middleware;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.handler.CustomRequestHandler;
import com.mtlevine0.handler.RequestRouter;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MiddlewareService {

    private static List<Middleware> middlewares;
    private int routerIndex = 0;

    public MiddlewareService(RequestRouter router) {
        middlewares = new ArrayList<>();
        middlewares.add(new RouterMiddleware(router));
        if(FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.REQUEST_LOGGING)) {
            registerPreRouter(new LoggingMiddleware());
        }
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
                middleware.handle(request, response);
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
