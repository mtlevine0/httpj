package com.mtlevine0.router.middleware;

import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;

import java.io.IOException;

public interface Middleware {

    public void handle(HttpRequest request, HttpResponse response) throws IOException;

}
