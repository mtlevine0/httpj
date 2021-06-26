package com.mtlevine0.handler;

import com.mtlevine0.FeatureFlag;
import com.mtlevine0.FeatureFlagContext;
import com.mtlevine0.exception.MethodNotAllowedException;
import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.*;

public class RequestRouter {
    private Map<Route, RequestHandler> handlers;

    public RequestRouter(String basePath) {
        handlers = new LinkedHashMap<>();
        registerRoute("*", HttpMethod.HEAD, new DefaultRequestHandler(basePath));
        if (FeatureFlagContext.getInstance().isFeatureActive(FeatureFlag.STATIC_FILE_SERVER)) {
            registerRoute("*", HttpMethod.GET, new StaticResourceRequestHandler(basePath));
        }
    }

    public HttpResponse route(HttpRequest httpRequest) throws AccessDeniedException, NoSuchFileException,
            URISyntaxException, MethodNotAllowedException {
        HttpResponse response = handleRoute(httpRequest);
        if (isWildCardPath(httpRequest, response) && Objects.isNull(response)) {
            response = handlers.get(new Route("*", httpRequest.getMethod())).handleRequest(httpRequest);
        }
        handleRouteNotMatched(httpRequest, response);
        return response;
    }

    private HttpResponse handleRoute(HttpRequest httpRequest) throws URISyntaxException, AccessDeniedException, NoSuchFileException {
        HttpResponse response = null;
        Route route = new Route(httpRequest.getPath(), httpRequest.getMethod());
        if (handlers.containsKey(route)) {
            response = handlers.get(route).handleRequest(httpRequest);
        }
        return response;
    }

    private boolean isWildCardPath(HttpRequest request, HttpResponse response) {
        return handlers.containsKey(new Route("*", request.getMethod())) ;
    }

    private void handleRouteNotMatched(HttpRequest httpRequest, HttpResponse response) {
        if (Objects.isNull(response)) {
            throw new MethodNotAllowedException(httpRequest.getMethod() + " - " +
                    httpRequest.getPath() + ": Not Implemented.");
        }
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
            this.handlers.put(new Route(path, HttpMethod.HEAD), requestHandler);
        }
        this.handlers.put(new Route(path, httpMethod), requestHandler);
    }

    public Map<Route, RequestHandler> getHandlers() {
        return this.handlers;
    }

    private class Route {
        private String path;
        private HttpMethod method;

        public Route(String path, HttpMethod httpMethod) {
            this.path = path;
            this.method = httpMethod;
        }

        public String getPath() {
            return path;
        }

        public HttpMethod getMethod() {
            return method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Route route = (Route) o;
            return Objects.equals(path, route.path) && method == route.method;
        }

        @Override
        public int hashCode() {
            return Objects.hash(path, method);
        }
    }
}
