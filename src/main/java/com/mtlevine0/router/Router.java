package com.mtlevine0.router;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;
import com.mtlevine0.httpj.exception.MethodNotAllowedException;
import com.mtlevine0.httpj.request.HttpMethod;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.router.handlers.*;
import com.mtlevine0.router.middleware.Middleware;

import java.io.IOException;
import java.util.*;

public class Router {
    private List<Middleware> preRouteHandlerMiddlewares;
    private Map<Route, RequestHandler> routeHandlers;
    private List<Middleware> postRouteHandlerMiddlewares;

    public Router(String basePath) {
        routeHandlers = new LinkedHashMap<>();
        registerRoute("/routes", HttpMethod.GET, new RouteInfoHandler(this));
        registerRoute("*", HttpMethod.HEAD, new DefaultCustomRequestHandler());
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.STATIC_FILE_SERVER)) {
            registerRoute("*", HttpMethod.GET, new StaticResourceRequestHandler(basePath));
        }
    }

    public HttpResponse route(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, MethodNotAllowedException {
        executeMiddleware(preRouteHandlerMiddlewares, httpRequest, httpResponse);
        if (matchesRoute(httpRequest)) {
            handleRoute(httpRequest, httpResponse);
        } else {
            if (isWildCardPath(httpRequest)) {
                handleWildCardRoute(httpRequest, httpResponse);
            } else {
                handleRouteNotMatched(httpRequest);
            }
        }
        executeMiddleware(postRouteHandlerMiddlewares, httpRequest, httpResponse);
        return httpResponse;
    }

    public void registerPreRouteHandlerMiddleware(Middleware middleware) {
        this.preRouteHandlerMiddlewares.add(middleware);
    }

    public void registerPostRouterHandlerMiddleware(Middleware middleware) {
        this.postRouteHandlerMiddlewares.add(middleware);
    }

    public void registerRoute(String path, HttpMethod httpMethod, RequestHandler requestHandler) {
        List<Route> routesMatchedByPath = findRoutesByPath(path);
        for (Route route : routesMatchedByPath) {
            if (route.getPath().equals(path) && route.getMethod().equals(httpMethod)) {
                throw new IllegalArgumentException(httpMethod + " - " + path + " - " + requestHandler.getClass() +
                        ": Conflicts with the following routes:\n" +
                        route.getMethod() + " - " + route.getPath() + " - " + routeHandlers.get(route).getClass());
            }
        }
        if (path.equals("*") && (requestHandler instanceof CustomRequestHandler)) {
            throw new IllegalArgumentException("Class of type CustomRequestHandler cannot be registered to a wildcard path.");
        }
        if (requestHandler instanceof CustomRequestHandler) {
            this.routeHandlers.put(new Route(path, HttpMethod.HEAD), new DefaultCustomRequestHandler());
        }
        this.routeHandlers.put(new Route(path, httpMethod), requestHandler);
    }

    public Map<Route, RequestHandler> getRouteHandlers() {
        return this.routeHandlers;
    }

    private void executeMiddleware(List<Middleware> middlewares, HttpRequest httpRequest, HttpResponse httpResponse) {
        if (Objects.isNull(middlewares)) {
            return;
        }
        for (Middleware middleware : middlewares) {
            try {
                middleware.handle(httpRequest, httpResponse);
            } catch (Exception e) {

            }
        }
    }

    private void handleWildCardRoute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        routeHandlers.get(new Route("*", httpRequest.getMethod())).handleRequest(httpRequest, httpResponse);
    }

    private boolean isCustomHandlerRegistered(HttpRequest httpRequest) {
        return routeHandlers.containsKey(new Route(httpRequest.getPath(), httpRequest.getMethod())) &&
                routeHandlers.get(new Route(httpRequest.getPath(), httpRequest.getMethod())) instanceof CustomRequestHandler;
    }

    private void handleRoute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (matchesRoute(httpRequest)) {
            Route route = new Route(httpRequest.getPath(), httpRequest.getMethod());
            routeHandlers.get(route).handleRequest(httpRequest, httpResponse);
        }
    }

    private boolean matchesRoute(HttpRequest httpRequest) {
        Route route = new Route(httpRequest.getPath(), httpRequest.getMethod());
        return routeHandlers.containsKey(route);
    }

    private boolean isWildCardPath(HttpRequest request) {
        return routeHandlers.containsKey(new Route("*", request.getMethod())) ;
    }

    private void handleRouteNotMatched(HttpRequest httpRequest) {
        throw new MethodNotAllowedException(httpRequest.getMethod() + " - " +
                httpRequest.getPath() + ": Not Implemented.");

    }

    private List<Route> findRoutesByPath(String path) {
        List<Route> matchedRoutes = new ArrayList<>();
        for (Route route : routeHandlers.keySet()) {
            if (route.getPath().equals(path)) {
                matchedRoutes.add(route);
            }
        }
        return matchedRoutes;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Route route : routeHandlers.keySet()) {
            sb.append(route.getMethod() + " - " + route.getPath() + " - " + routeHandlers.get(route).getClass().getName() + "\n");
        }
        return sb.toString();
    }
}
