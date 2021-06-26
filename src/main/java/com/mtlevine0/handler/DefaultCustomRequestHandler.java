package com.mtlevine0.handler;

import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;
import com.mtlevine0.response.HttpStatus;

public class DefaultCustomRequestHandler implements RequestHandler{
    @Override
    public HttpResponse handleRequest(HttpRequest httpRequest) {
        return HttpResponse.builder().status(HttpStatus.OK).build();
    }
}
