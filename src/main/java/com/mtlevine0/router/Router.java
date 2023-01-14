package com.mtlevine0.router;

import com.mtlevine0.httpj.FeatureFlag;
import com.mtlevine0.httpj.FeatureFlagContext;
import com.mtlevine0.httpj.exception.MethodNotAllowedException;
import com.mtlevine0.httpj.request.HttpMethod;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;
import com.mtlevine0.router.handlers.CustomRequestHandler;
import com.mtlevine0.router.handlers.DefaultCustomRequestHandler;
import com.mtlevine0.router.handlers.RequestHandler;
import com.mtlevine0.router.handlers.StaticResourceRequestHandler;

import java.io.IOException;
import java.util.*;

public class Router {
    private Map<Route, RequestHandler> handlers;

    public Router(String basePath) {
        handlers = new LinkedHashMap<>();
        registerRoute("/routes", HttpMethod.GET, new RouteInfoHandler(this));
        registerRoute("*", HttpMethod.HEAD, new DefaultCustomRequestHandler());
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.STATIC_FILE_SERVER)) {
            registerRoute("*", HttpMethod.GET, new StaticResourceRequestHandler(basePath));
        }
    }

    public HttpResponse route(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException, MethodNotAllowedException {
        if (matchesRoute(httpRequest)) {
            handleRoute(httpRequest, httpResponse);
        } else {
            if (isWildCardPath(httpRequest)) {
                handleWildCardRoute(httpRequest, httpResponse);
            } else {
                handleRouteNotMatched(httpRequest);
            }
        }
        return httpResponse;
    }

    private void handleWildCardRoute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        handlers.get(new Route("*", httpRequest.getMethod())).handleRequest(httpRequest, httpResponse);
    }

    private boolean isCustomHandlerRegistered(HttpRequest httpRequest) {
        return handlers.containsKey(new Route(httpRequest.getPath(), httpRequest.getMethod())) &&
                handlers.get(new Route(httpRequest.getPath(), httpRequest.getMethod())) instanceof CustomRequestHandler;
    }

    private void handleRoute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (matchesRoute(httpRequest)) {
            Route route = new Route(httpRequest.getPath(), httpRequest.getMethod());
            handlers.get(route).handleRequest(httpRequest, httpResponse);
        }
    }

    public boolean matchesRoute(HttpRequest httpRequest) {
        Route route = new Route(httpRequest.getPath(), httpRequest.getMethod());
        return handlers.containsKey(route);
    }

    private boolean isWildCardPath(HttpRequest request) {
        return handlers.containsKey(new Route("*", request.getMethod())) ;
    }

    private void handleRouteNotMatched(HttpRequest httpRequest) {
        throw new MethodNotAllowedException(httpRequest.getMethod() + " - " +
                httpRequest.getPath() + ": Not Implemented.");

    }

    private List<Route> findRoutesByPath(String path) {
        List<Route> matchedRoutes = new ArrayList<>();
        for (Route route : handlers.keySet()) {
            if (route.getPath().equals(path)) {
                matchedRoutes.add(route);
            }
        }
        return matchedRoutes;
    }

    public void registerRoute(String path, HttpMethod httpMethod, RequestHandler requestHandler) {
        List<Route> routesMatchedByPath = findRoutesByPath(path);
        for (Route route : routesMatchedByPath) {
            if (route.getPath().equals(path) && route.getMethod().equals(httpMethod)) {
                throw new IllegalArgumentException(httpMethod + " - " + path + " - " + requestHandler.getClass() +
                    ": Conflicts with the following routes:\n" +
                    route.getMethod() + " - " + route.getPath() + " - " + handlers.get(route).getClass());
            }
        }
        if (path.equals("*") && (requestHandler instanceof CustomRequestHandler)) {
            throw new IllegalArgumentException("Class of type CustomRequestHandler cannot be registered to a wildcard path.");
        }
        if (requestHandler instanceof CustomRequestHandler) {
            this.handlers.put(new Route(path, HttpMethod.HEAD), new DefaultCustomRequestHandler());
        }
        this.handlers.put(new Route(path, httpMethod), requestHandler);
    }

    public Map<Route, RequestHandler> getHandlers() {
        return this.handlers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Route route : handlers.keySet()) {
            sb.append(route.getMethod() + " - " + route.getPath() + " - " + handlers.get(route).getClass().getName() + "\n");
        }
        return sb.toString();
    }

    public class RouteInfoHandler implements CustomRequestHandler {
        private Router router;

        public RouteInfoHandler(Router router) {
            this.router = router;
        }

        @Override
        public void handleRequest(HttpRequest httpRequest, HttpResponse response) {
            response.setBody(router.toString().getBytes());
            response.setStatus(HttpStatus.OK);
        }
    }
}
