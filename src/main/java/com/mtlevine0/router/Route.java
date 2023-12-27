package com.mtlevine0.router;

import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.RequestHandler;
import lombok.SneakyThrows;

import java.util.Objects;

public class Route {
    private String path;
    private HttpMethod method;
    private RequestHandler requestHandler;
    private AdvancedRouter router;

    public Route(String path, AdvancedRouter router) {
        this(path, null, null);
        this.router = router;
    }

    public Route(String path, HttpMethod httpMethod, RequestHandler requestHandler) {
        this.path = path;
        this.method = httpMethod;
        this.requestHandler = requestHandler;
    }

    @SneakyThrows
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (Objects.nonNull(router)) {
            router.handleRequest(httpRequest, httpResponse);
        } else {
            requestHandler.handleRequest(httpRequest, httpResponse);
        }
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public AdvancedRouter getRouter() {
        return router;
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
