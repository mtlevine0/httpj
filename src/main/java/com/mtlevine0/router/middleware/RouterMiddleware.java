package com.mtlevine0.router.middleware;

import com.mtlevine0.router.Router;
import com.mtlevine0.httpj.request.HttpRequest;
import com.mtlevine0.httpj.response.HttpResponse;

import java.io.IOException;

public class RouterMiddleware implements Middleware {
    private Router router;

    public RouterMiddleware(Router router) {
        this.router = router;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws IOException {
        router.route(request, response);

    }
}
