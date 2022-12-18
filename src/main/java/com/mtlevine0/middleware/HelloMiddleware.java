package com.mtlevine0.middleware;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.util.logging.Logger;

public class HelloMiddleware implements Middleware {
    private static final Logger LOGGER = Logger.getLogger(HelloMiddleware.class.getName());

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        LOGGER.info("Hello!!!");
    }
}
