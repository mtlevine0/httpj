package com.mtlevine0.router;

import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;

public class DefaultCustomRequestHandler implements RequestHandler{
    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
    }
}
