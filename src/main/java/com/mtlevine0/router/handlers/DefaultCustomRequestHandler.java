package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.response.HttpStatus;

public class DefaultCustomRequestHandler implements RequestHandler {
    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
    }
}
