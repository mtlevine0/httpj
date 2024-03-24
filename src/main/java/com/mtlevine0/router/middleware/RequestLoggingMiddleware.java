package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;

import java.util.*;
import java.util.logging.Logger;

public class RequestLoggingMiddleware implements MiddlewareRequestHandler {
    private static final Logger LOGGER = Logger.getLogger(RequestLoggingMiddleware.class.getName());

    @Override
    public Middleware.Status handleRequest(HttpRequest request, HttpResponse response) {
        Map<String, String> headers = request.getHeaders();
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod() + " - " + request.getPath() + "\n");
        for(String headerKey: headers.keySet()) {
            sb.append(headerKey + ": " +  headers.get(headerKey) + "\n");
        }
        LOGGER.info(Thread.currentThread().getName() + " - " + sb);
        return Middleware.Status.CONTINUE;
    }
}
