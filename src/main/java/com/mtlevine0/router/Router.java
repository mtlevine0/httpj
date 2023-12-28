package com.mtlevine0.router;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.router.handlers.RouteInfoHandler;
import com.mtlevine0.router.middleware.MiddlewareHandler;

import java.util.HashSet;
import java.util.Set;

public abstract class Router extends MiddlewareHandler implements RequestHandler {
    private Set<Route> routes = new HashSet<>();

    public Router() {
        registerRoute(new Route("/routes", HttpMethod.GET, new RouteInfoHandler(this)));
    }

    public abstract void registerRoute(Route route);

    public Set<Route> getRoutes() {
        return routes;
    }

    @Override
    public String toString() {
        StringBuilder body = new StringBuilder();
        for (Route route : getRoutes()) {
            body.append(route.getMethod() + " " + route.getPath() + "\n");
        }
        return body.toString();
    }
}
