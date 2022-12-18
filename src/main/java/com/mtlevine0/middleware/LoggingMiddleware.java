package com.mtlevine0.middleware;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class LoggingMiddleware implements Middleware {
    private static final Logger LOGGER = Logger.getLogger(LoggingMiddleware.class.getName());

    @Override
    public void handle(HttpRequest request, HttpResponse response, Middleware next) {
        Map<String, String> headers = request.getHeaders();
        for(String headerKey: headers.keySet()) {
            LOGGER.info(headerKey + ": " +  headers.get(headerKey));
        }
        if (Objects.nonNull(next)) {
            next.handle(request, response, null);
        }
    }
}
