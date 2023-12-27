package com.mtlevine0.router.handlers;

import com.mtlevine0.httpj.common.CustomRequestHandler;
import com.mtlevine0.httpj.common.RequestHandler;
import com.mtlevine0.httpj.common.request.HttpRequest;
import com.mtlevine0.httpj.common.response.HttpResponse;
import com.mtlevine0.httpj.common.response.HttpStatus;
import com.mtlevine0.router.AdvancedRouter;
import com.mtlevine0.router.Router;

public class RouteInfoHandler implements RequestHandler {
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
