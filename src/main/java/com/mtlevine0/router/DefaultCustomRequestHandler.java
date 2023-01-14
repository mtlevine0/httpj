package com.mtlevine0.router;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

public class DefaultCustomRequestHandler implements RequestHandler{
    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        httpResponse.setStatus(HttpStatus.OK);
    }
}
