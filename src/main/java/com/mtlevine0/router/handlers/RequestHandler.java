package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;

import java.io.IOException;

public interface RequestHandler {

    public void handleRequest(HttpRequest httpRequest, HttpResponse response) throws IOException;

}
