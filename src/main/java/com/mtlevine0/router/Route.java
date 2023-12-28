package com.mtlevine0.router;

import com.mtlevine0.httpj.common.request.HttpMethod;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.RequestHandler;

import java.util.Objects;

public class Route implements RequestHandler {
    private String path;
    private HttpMethod method;
    private RequestHandler requestHandler;

    public Route(String path, HttpMethod httpMethod, RequestHandler requestHandler) {
        this.path = path;
        this.method = httpMethod;
        this.requestHandler = requestHandler;
    }

    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        requestHandler.handleRequest(httpRequest, httpResponse);
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
