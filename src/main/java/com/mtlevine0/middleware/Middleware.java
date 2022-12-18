package com.mtlevine0.middleware;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

public interface Middleware {

    public void handle(HttpRequest request, HttpResponse response, Middleware next);

}
