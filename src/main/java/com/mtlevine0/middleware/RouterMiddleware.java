package com.mtlevine0.middleware;

import com.mtlevine0.handler.RequestRouter;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

import java.io.IOException;

public class RouterMiddleware implements Middleware {
    private RequestRouter router;

    public RouterMiddleware(RequestRouter router) {
        this.router = router;
    }

    @Override
    public Boolean handle(HttpRequest request, HttpResponse response) throws IOException {
        router.route(request, response);
        return false;
    }
}
