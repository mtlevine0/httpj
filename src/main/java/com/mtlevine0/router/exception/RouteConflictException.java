package com.mtlevine0.router.exception;

import com.mtlevine0.router.Route;

public class RouteConflictException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Route conflict occurred when registering route";
    private static final String DETAILED_MESSAGE = "Route conflict occurred when registering route with path: %s and method: %s";

    public RouteConflictException() {
        super(DETAILED_MESSAGE);
    }

    public RouteConflictException(Route route) {
        super(String.format(DETAILED_MESSAGE, route.getPath(), route.getMethod()));
    }
}
