package com.mtlevine0.middleware;

import com.mtlevine0.router.Router;
import com.mtlevine0.request.HttpRequest;
import com.mtlevine0.response.HttpResponse;

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
