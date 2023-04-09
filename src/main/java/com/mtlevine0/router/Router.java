package com.mtlevine0.router;

import com.mtlevine0.httpj.exception.MethodNotAllowedException;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.router.exception.RouteConflictException;

import java.util.*;

public class Router {
    private Set<Route> routes = new HashSet<>();

    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.route(httpRequest, httpResponse);
    }

    private void route(HttpRequest httpRequest, HttpResponse httpResponse) {
        for (Route route: routes) {
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

    public void registerRoute(Route route) {
        if (!routes.add(route)) throw new RouteConflictException(route);
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    private void handleRouteNotMatched(HttpRequest httpRequest) {
        throw new MethodNotAllowedException(httpRequest.getMethod() + " - " +
                httpRequest.getPath() + ": Not Implemented.");
    }

}
