package com.mtlevine0.middleware;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class MiddlewareService {

    private List<Middleware> middleware;

    public MiddlewareService() {
        middleware = new ArrayList<>();
    }

    public void register(Middleware middleware) {
        this.middleware.add(middleware);
    }

    public void execute(HttpRequest request, HttpResponse response) {
        if (middleware.size() > 0) {
            middleware.get(0).handle(request, response, middleware.get(1));
        }
    }

    public List<Middleware> getMiddleware() {
        return this.middleware;
    }

}
