package com.mtlevine0.router;

import com.mtlevine0.httpj.request.HttpMethod;

import java.util.Objects;

public class Route {
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
