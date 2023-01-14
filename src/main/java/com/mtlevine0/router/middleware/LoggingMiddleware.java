package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;

import java.util.Map;
import java.util.logging.Logger;

public class LoggingMiddleware implements Middleware {
    private static final Logger LOGGER = Logger.getLogger(LoggingMiddleware.class.getName());

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        Map<String, String> headers = request.getHeaders();
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod() + " - " + request.getPath() + "\n");
        for(String headerKey: headers.keySet()) {
            sb.append(headerKey + ": " +  headers.get(headerKey) + "\n");
        }
        LOGGER.info(Thread.currentThread().getName() + " - " + sb);
    }
}
