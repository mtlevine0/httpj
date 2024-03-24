package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Middleware {
    List<MiddlewareRequestHandler> MiddlewareRequestHandlers = new ArrayList<>();

    public void registerMiddleware(MiddlewareRequestHandler MiddlewareRequestHandler) {
        if (Objects.nonNull(MiddlewareRequestHandler)) {
            this.MiddlewareRequestHandlers.add(MiddlewareRequestHandler);
        } else {
            throw new IllegalArgumentException("MiddlewareRequestHandler can not be null");
        }
    }

    public Status execute(HttpRequest request, HttpResponse response) {
        for (MiddlewareRequestHandler middlewareRequestHandler : MiddlewareRequestHandlers) {
            if (middlewareRequestHandler.handleRequest(request, response).equals(Status.EXIT)) {
                return Status.EXIT;
            }
        }
        return Status.CONTINUE;
    }

    public enum Status {
        EXIT,
        CONTINUE
    }

}
