package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;

public interface RequestHandler {

    void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse);

}
