package com.mtlevine0.router;

import com.mtlevine0.httpj.common.exception.MethodNotAllowedException;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.router.exception.RouteConflictException;
import com.mtlevine0.router.middleware.GzipMiddleware;
import com.mtlevine0.router.middleware.RequestLoggingMiddleware;
import com.mtlevine0.router.middleware.Middleware;
import com.mtlevine0.router.middleware.ResponseLoggingMiddleware;

public class BasicRouter extends Router {

    public BasicRouter() {
        super.registerPreRequestMiddlewareHandler(new RequestLoggingMiddleware());
        super.registryPostRequestMiddlewareHandler(new ResponseLoggingMiddleware());
        super.registryPostRequestMiddlewareHandler(new GzipMiddleware());
    }

    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.route(httpRequest, httpResponse);
    }

    private void route(HttpRequest httpRequest, HttpResponse httpResponse) {
        for (Route route: getRoutes()) {
            if (httpRequest.getPath().equals(route.getPath()) && (httpRequest.getMethod().equals(route.getMethod()))) {
                super.handleRequest(httpRequest, httpResponse, route);
                return;
            }
        }
        handleRouteNotMatched(httpRequest);
    }

    @Override
    public void registerRoute(Route route) {
        if (!getRoutes().add(route)) throw new RouteConflictException(route);
    }

    private void handleRouteNotMatched(HttpRequest httpRequest) {
        throw new MethodNotAllowedException(httpRequest.getMethod() + " " +
                httpRequest.getPath() + " - Not Implemented");
    }
}
