package com.mtlevine0.handler;

import com.mtlevine0.exception.MethodNotAllowedException;
import com.mtlevine0.request.HttpMethod;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RequestRouter {
    private Map<Route, RequestHandler> routes;

    public RequestRouter() {
        routes = new LinkedHashMap<>();
    }

    public HttpResponse route(HttpRequest httpRequest) throws AccessDeniedException, NoSuchFileException,
            URISyntaxException, MethodNotAllowedException {
        HttpResponse response;
        response = handleRoute(httpRequest);
        if (isWildCardPath(httpRequest, response)) {
            response = routes.get(new Route("*", httpRequest.getMethod())).handleRequest(httpRequest);
        }
        handleRouteNotMatched(httpRequest, response);
        return response;
    }

    private HttpResponse handleRoute(HttpRequest httpRequest) throws URISyntaxException, AccessDeniedException, NoSuchFileException {
        HttpResponse response = null;
        for (Route route : routes.keySet()) {
            if (route.getPath().equals(httpRequest.getPath()) && route.getMethod().equals(httpRequest.getMethod())) {
                response = routes.get(route).handleRequest(httpRequest);
            }
        }
        return response;
    }

    private boolean isWildCardPath(HttpRequest request, HttpResponse response) {
        Route route = new Route("*", request.getMethod());
        return routes.containsKey(route) && Objects.isNull(response);
    }

    private void handleRouteNotMatched(HttpRequest httpRequest, HttpResponse response) {
        if (Objects.isNull(response)) {
            throw new MethodNotAllowedException(httpRequest.getMethod() + " - " +
                    httpRequest.getPath() + ": Not Implemented.");
        }
    }

    public void registerRoute(String path, HttpMethod httpMethod, RequestHandler requestHandler) {
        this.routes.put(new Route(path, httpMethod), requestHandler);
    }

    public Map<Route, RequestHandler> getRoutes() {
        return this.routes;
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
