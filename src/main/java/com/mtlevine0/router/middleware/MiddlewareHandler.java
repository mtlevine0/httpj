package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;

public class MiddlewareHandler {
    private Middleware preRequestMiddleware = new Middleware();
    private Middleware postRequestMiddleware = new Middleware();

    public void registerPreRequestMiddlewareHandler(MiddlewareRequestHandler middlewareRequestHandler) {
        this.preRequestMiddleware.registerMiddleware(middlewareRequestHandler);
    }

    public void registerPostRequestMiddlewareHandler(MiddlewareRequestHandler middlewareRequestHandler) {
        this.postRequestMiddleware.registerMiddleware(middlewareRequestHandler);
    }

    public void handleRequest(HttpRequest request, HttpResponse response, RequestHandler requestHandler) {
        if (preRequestMiddleware.execute(request, response).equals(Middleware.Status.CONTINUE)) {
            requestHandler.handleRequest(request, response);
        }
        postRequestMiddleware.execute(request, response);
    }

}
