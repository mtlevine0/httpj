package com.mtlevine0.middleware;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class MiddlewareService {

    private List<Middleware> middlewares;

    public MiddlewareService() {
        middlewares = new ArrayList<>();
        middlewares.add(new LoggingMiddleware());
        middlewares.add(new HelloMiddleware());
    }

    public void register(Middleware middleware) {
        this.middlewares.add(middleware);
    }

    public void execute(HttpRequest request, HttpResponse response) {
        for(Middleware middleware: middlewares) {
            middleware.handle(request, response);
        }
    }

    public List<Middleware> getMiddlewares() {
        return this.middlewares;
    }

}
