package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;

public interface MiddlewareRequestHandler {

    Middleware.Status handleRequest(HttpRequest request, HttpResponse response);

}
