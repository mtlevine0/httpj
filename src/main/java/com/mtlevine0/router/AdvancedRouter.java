package com.mtlevine0.router;

import com.mtlevine0.httpj.common.exception.MethodNotAllowedException;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.router.exception.RouteConflictException;

import java.util.*;

public class AdvancedRouter extends Router {
    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.route(httpRequest, httpResponse);
    }

    private void route(HttpRequest httpRequest, HttpResponse httpResponse) {
        for (Route route: getRoutes()) {
            if (httpRequest.getPath().equals(route.getPath()) && (httpRequest.getMethod().equals(route.getMethod()))) {
                route.handleRequest(httpRequest, httpResponse);
                return;
            } else if (Objects.nonNull(route.getRouter())) {
                String registeredPath = route.getPath();
                httpRequest.setPath(httpRequest.getPath().replace(registeredPath, ""));
                route.handleRequest(httpRequest, httpResponse);
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
        throw new MethodNotAllowedException(httpRequest.getMethod() + " - " +
                httpRequest.getPath() + ": Not Implemented.");
    }

}
