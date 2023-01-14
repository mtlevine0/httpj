package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;
import com.mtlevine0.httpj.response.HttpStatus;
import com.mtlevine0.router.Router;

public class RouteInfoHandler implements CustomRequestHandler {
    private Router router;

    public RouteInfoHandler(Router router) {
        this.router = router;
    }

    @Override
    public void handleRequest(HttpRequest httpRequest, HttpResponse response) {
        response.setBody(router.toString().getBytes());
        response.setStatus(HttpStatus.OK);
    }
}
